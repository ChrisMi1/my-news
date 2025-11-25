package com.techie.mynews.controller;

import com.techie.mynews.commons.datatable.SearchRequest;
import com.techie.mynews.commons.datatable.SearchResponse;
import com.techie.mynews.dto.request.SourceCriteriaRequest;
import com.techie.mynews.service.SourceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sources")
public class SourceController {

    private final SourceSearchService sourceSearchService;

    @PostMapping("/search")
    public ResponseEntity<SearchResponse<SourceCriteriaRequest>> search(@RequestBody SearchRequest<SourceCriteriaRequest> sourceSearchRequest) {
        SearchResponse<SourceCriteriaRequest> response = sourceSearchService.search(sourceSearchRequest);
        return ResponseEntity.ok(response);
    }
}
