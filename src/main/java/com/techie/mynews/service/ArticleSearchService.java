package com.techie.mynews.service;

import com.techie.mynews.commons.datatable.DatatableResponseFactory;
import com.techie.mynews.commons.datatable.SearchRequest;
import com.techie.mynews.commons.datatable.SearchResponse;
import com.techie.mynews.dto.request.ArticleCriteriaRequest;
import com.techie.mynews.dto.request.SourceCriteriaRequest;
import com.techie.mynews.dto.response.ArticleSearchResponse;
import com.techie.mynews.dto.response.SourceSearchResponse;
import com.techie.mynews.model.Article;
import com.techie.mynews.model.Source;
import com.techie.mynews.repository.ArticleRepository;
import com.techie.mynews.specification.ArticleSpecification;
import com.techie.mynews.specification.SourceSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ArticleSearchService  extends DatatableResponseFactory<ArticleCriteriaRequest, ArticleSearchResponse> {
    private final ArticleRepository articleRepository;

    public SearchResponse<ArticleCriteriaRequest> search(SearchRequest<ArticleCriteriaRequest> searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPageNumber(),searchRequest.getPageTotalContentSize(), Sort.by(Sort.Order.desc("publishedAt")));
        Specification<Article> spec = Specification.allOf(ArticleSpecification.articleSpecifications(searchRequest.getSearchCriteria()));
        Page<ArticleSearchResponse> responsePage = articleRepository.findAll(spec,pageable).map(this::mapToArticleSearchResponse);
        return convertToSearchResponse(responsePage,searchRequest);
    }

    private ArticleSearchResponse mapToArticleSearchResponse(Article article) {
        ArticleSearchResponse articleSearchResponse = new ArticleSearchResponse();
        articleSearchResponse.setSourceName(article.getSource().getName());
        articleSearchResponse.setAuthor(article.getAuthor());
        articleSearchResponse.setContent(article.getContent());
        articleSearchResponse.setDescription(article.getDescription());
        articleSearchResponse.setTitle(article.getTitle());
        articleSearchResponse.setUrl(article.getUrl());
        articleSearchResponse.setUrlToImage(article.getUrlToImage());
        articleSearchResponse.setPublishedAt(article.getPublishedAt());
        return articleSearchResponse;
    }

}
