package com.techie.mynews.commons.datatable;

import org.springframework.data.domain.Page;

public abstract class DatatableResponseFactory<C extends SearchCriteria,E> {
    public SearchResponse<C> convertToSearchResponse(Page<E> paginatedData, SearchRequest<C> searchRequest) {
        SearchResponse<C> searchResponse = new SearchResponse<>();
        searchResponse.setContent(paginatedData.getContent());
        searchResponse.setPageNumber(paginatedData.getNumber());
        searchResponse.setPageContentSize(paginatedData.getNumberOfElements());
        searchResponse.setTotalPages(paginatedData.getTotalPages());
        searchResponse.setPageTotalContentSize(paginatedData.getSize());
        searchResponse.setSortCriteria(searchRequest.getSortCriteriaList());
        searchResponse.setSearchCriteria(searchRequest.getSearchCriteria());

        return searchResponse;
    }
}
