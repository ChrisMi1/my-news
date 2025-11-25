package com.techie.mynews.service;

import com.techie.mynews.commons.datatable.DatatableResponseFactory;
import com.techie.mynews.commons.datatable.DatatableSortCriteria;
import com.techie.mynews.commons.datatable.SearchRequest;
import com.techie.mynews.commons.datatable.SearchResponse;
import com.techie.mynews.dto.request.SourceCriteriaRequest;
import com.techie.mynews.dto.response.SourceSearchResponse;
import com.techie.mynews.model.Source;
import com.techie.mynews.repository.SourceRepository;
import com.techie.mynews.specification.SourceSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SourceSearchService extends DatatableResponseFactory<SourceCriteriaRequest, SourceSearchResponse> {
    private final SourceRepository sourceRepository;
    public SearchResponse<SourceCriteriaRequest> search(SearchRequest<SourceCriteriaRequest> searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPageNumber(),searchRequest.getPageTotalContentSize(), Sort.by(Sort.Order.asc("name")));
        Specification<Source> spec = Specification.allOf(SourceSpecification.sourceSpecifications(searchRequest.getSearchCriteria()));
        Page<SourceSearchResponse> responsePage = sourceRepository.findAll(spec,pageable).map(this::mapToSourceSearchResponse);
        return convertToSearchResponse(responsePage,searchRequest);
    }

    private SourceSearchResponse mapToSourceSearchResponse(Source source) {
        return SourceSearchResponse.builder()
                .id(source.getId())
                .externalId(source.getExternalId())
                .name(source.getName())
                .url(source.getUrl())
                .description(source.getDescription())
                .category(source.getCategory())
                .language(source.getLanguage())
                .country(source.getCountry())
                .build();
    }
}
