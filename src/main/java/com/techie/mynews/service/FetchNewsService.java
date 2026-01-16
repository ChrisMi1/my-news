package com.techie.mynews.service;

import com.techie.mynews.dto.response.ArticleResponse;
import com.techie.mynews.dto.response.NewsApiResponse;
import com.techie.mynews.model.Article;
import com.techie.mynews.model.Source;
import com.techie.mynews.repository.ArticleRepository;
import com.techie.mynews.repository.SourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FetchNewsService {
    private final SourceRepository sourceRepository;
    private final NewsApiClient newsApiClient;
    private final ArticleRepository articleRepository;
    private final Executor executor;
    public void fetchAndStoreNews() {
        List<String> externalIdList = sourceRepository.findUniqueExternalIds();

        if (externalIdList.isEmpty()) {
            log.info("No external ids found. Nothing to fetch");
            return;
        }

        List<List<String>> batchesOfSources = partitionArray(externalIdList,5);
        CompletableFuture<Void> future = fetchArticlesAsync(batchesOfSources);
        future.join();
    }

    @Async("articleExecutor")
    public CompletableFuture<Void> fetchArticlesAsync(List<List<String>> sources) {

        List<CompletableFuture<Void>> futures = sources.stream()
                .map(batch -> CompletableFuture.runAsync(() -> {
                    String externalIds = String.join(",", batch);
                    NewsApiResponse apiResponse = newsApiClient.fetchNewsFromNewsApi(externalIds);

                    if (apiResponse != null) saveArticles(apiResponse);
                }, executor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void saveArticles(NewsApiResponse apiResponse) {
        for (ArticleResponse articleResponse : apiResponse.getArticles()) {
            if (articleRepository.existsByUrl(articleResponse.getUrl())) {
                log.info("Article with url: " + articleResponse.getUrl() + " already fetched continue...");
                continue;
            }
            log.info("Find source for external id: " + articleResponse.getSourceDto().getExternalId());
            Source source = sourceRepository.findSourceByExternalId(articleResponse.getSourceDto().getExternalId());
            Article article = mapArticleResponseToArticle(source, articleResponse);
            try{
                articleRepository.save(article);
            } catch (DataIntegrityViolationException ex) {
                log.info("Article already exists, skipping: " + article.getUrl());
            }
        }
    }
    private List<List<String>> partitionArray(List<String> externalIds,Integer size) {
        List<List<String>> parts = new ArrayList<>();
        for (int i = 0; i < externalIds.size(); i += size) {
            parts.add(externalIds.subList(i,Math.min(i + size,externalIds.size())));
        }
        return parts;
    }

    private Article mapArticleResponseToArticle(Source source, ArticleResponse articleResponse) {
        Article article = new Article();
        article.setSource(source);
        article.setAuthor(articleResponse.getAuthor());
        article.setDescription(articleResponse.getDescription());
        article.setPublishedAt(parsePublishedAt(articleResponse.getPublishedAt()));
        article.setContent(cleanContent(articleResponse.getContent()));
        article.setUrl(articleResponse.getUrl());
        article.setUrlToImage(articleResponse.getUrlToImage());
        article.setTitle(articleResponse.getTitle());
        return article;
    }

    public LocalDateTime parsePublishedAt(String publishedAt) {
        try {
            return OffsetDateTime.parse(publishedAt).toLocalDateTime();
        } catch (Exception ex) {
            log.warn("Failed parsing as OffsetDateTime: '{}'. Reason: {}", publishedAt, ex.getMessage());
        }

        try {
            return LocalDateTime.parse(publishedAt);
        } catch (Exception ex) {
            log.error("Failed parsing as LocalDateTime: '{}'. Reason: {}", publishedAt, ex.getMessage());
        }

        return null;
    }

    public String cleanContent(String content) {
        return !StringUtils.isBlank(content) ? content.replaceAll("\\s*\\[\\+?\\d+\\s*chars?]\\s*", "").trim() : null;
    }

}
