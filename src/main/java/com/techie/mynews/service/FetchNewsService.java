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
import java.util.Map;
import java.util.Set;
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
    private final ArticleService articleService;
    private static final int BATCH_SIZE = 5;

    public void fetchAndStoreNews() {
        List<String> externalIdList = sourceRepository.findUniqueExternalIds();

        if (externalIdList.isEmpty()) {
            log.info("No external ids found. Nothing to fetch");
            return;
        }

        List<List<String>> batchesOfSources = partitionArray(externalIdList,BATCH_SIZE);

        List<CompletableFuture<Void>> pipelineFutures = batchesOfSources.stream()
                .map(batch -> newsApiClient.fetchNewsFromNewsApi(String.join(",", batch))
                        .thenAcceptAsync(apiResponse -> {
                            if (apiResponse != null) {
                                processApiResponse(apiResponse);
                            }
                        }, executor))
                .toList();

        CompletableFuture.allOf(pipelineFutures.toArray(new CompletableFuture[0])).join();
    }

    private void processApiResponse(NewsApiResponse apiResponse) {
        if (apiResponse == null || apiResponse.getArticles().isEmpty()) return;
        log.info("Extract the incoming urls from news api response");
        List<String> incomingUrls = apiResponse.getArticles().stream()
                .map(ArticleResponse::getUrl)
                .filter(StringUtils::isNotBlank)
                .toList();
        log.info("Extract the existing urls from db");
        Set<String> existingUrls = articleRepository.findExistingUrls(incomingUrls);
        log.info("Extract the sources by external ids from api response");
        Set<String> sourceExternalIds = apiResponse.getArticles().stream()
                .map(a -> a.getSourceDto().getExternalId())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        log.info("Map every external id with the ref source");
        Map<String, Source> sourceMap = sourceRepository.findByExternalIds(sourceExternalIds)
                .stream()
                .collect(Collectors.toMap(Source::getExternalId, s -> s));


        List<Article> newArticles = new ArrayList<>();
        log.info("Add in newArticles list only the urls that does not exist in our existingUrls set");

        for (ArticleResponse dto : apiResponse.getArticles()) {
            if (!existingUrls.contains(dto.getUrl())) {
                Source source = sourceMap.get(dto.getSourceDto().getExternalId());
                if (source == null) {
                    log.info("No source found with external id = " + dto.getSourceDto().getExternalId());
                    continue;
                }
                newArticles.add(mapArticleResponseToArticle(source, dto));
            }
        }

        log.info("Found {} in the current batch. Starting the insertion", newArticles.size());
        try {
            articleService.persistArticleBatch(newArticles);
            log.info("Successfully persisted batch of size {}", newArticles.size());
        } catch (Exception ex) {
            log.error("Failed to persist batch. Skipping and continuing to next batch.", ex);
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
