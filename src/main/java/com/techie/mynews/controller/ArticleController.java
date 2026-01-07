package com.techie.mynews.controller;

import com.techie.mynews.commons.datatable.SearchRequest;
import com.techie.mynews.commons.datatable.SearchResponse;
import com.techie.mynews.dto.request.ArticleCriteriaRequest;
import com.techie.mynews.dto.request.SourceCriteriaRequest;
import com.techie.mynews.service.ArticleSearchService;
import com.techie.mynews.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleSearchService articleSearchService;

    @PostMapping("/search")
    public ResponseEntity<SearchResponse<ArticleCriteriaRequest>> search(@RequestBody SearchRequest<ArticleCriteriaRequest> articleSearchRequest) {
        SearchResponse<ArticleCriteriaRequest> response = articleSearchService.search(articleSearchRequest);
        return ResponseEntity.ok(response);
    }
}
