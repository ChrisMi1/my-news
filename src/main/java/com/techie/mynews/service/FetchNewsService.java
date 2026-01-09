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
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FetchNewsService {
    private final SourceRepository sourceRepository;
    private final NewsApiClient newsApiClient;
    private final ArticleRepository articleRepository;
    public void fetchAndStoreNews() {

        List<String> externalIdList = sourceRepository.findUniqueExternalIds();
        if (externalIdList.isEmpty()) {
            log.info("No external ids found. Nothing to fetch");
            return;
        }

        String externalIds = String.join(",", externalIdList);
        NewsApiResponse apiResponse = newsApiClient.fetchNewsFromNewsApi(externalIds);

        if (apiResponse == null) {
            log.warn("News API returned null response");
            return;
        }

        for (ArticleResponse articleResponse : apiResponse.getArticles()) {
            log.info("Check if article already fetched");

            if (articleRepository.existsByUrl(articleResponse.getUrl())) continue;

            log.info("Find source for external id: " + articleResponse.getSourceDto().getExternalId());
            Source source = sourceRepository.findSourceByExternalId(articleResponse.getSourceDto().getExternalId());

            Article article = mapArticleResponseToArticle(source,articleResponse);

            log.info("Saving article with title: " + article.getTitle());
            articleRepository.save(article);
        }

    }

    private Article mapArticleResponseToArticle(Source source, ArticleResponse articleResponse) {
        Article article = new Article();
        article.setSource(source);
        article.setAuthor(articleResponse.getAuthor());
        article.setDescription(articleResponse.getDescription());
        article.setPublishedAt(OffsetDateTime.parse(articleResponse.getPublishedAt()).toLocalDateTime());
        article.setContent(cleanContent(articleResponse.getContent()));
        article.setUrl(articleResponse.getUrl());
        article.setUrlToImage(articleResponse.getUrlToImage());
        article.setTitle(articleResponse.getTitle());
        return article;
    }

    public String cleanContent(String content) {
        return content.replaceAll("\\s*\\[\\+?\\d+\\s*chars?]\\s*", "").trim();
    }

}
