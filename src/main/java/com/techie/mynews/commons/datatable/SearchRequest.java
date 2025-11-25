package com.techie.mynews.commons.datatable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
@Getter
@Setter
@Accessors(chain = true)
public class SearchRequest<C extends SearchCriteria> {
    private C searchCriteria;
    private List<DatatableSortCriteria> sortCriteriaList;
    private Integer pageTotalContentSize = 10;
    private Integer pageNumber = 0;

    public boolean hasSortCriteria() {
        return sortCriteriaList != null && !sortCriteriaList.isEmpty();
    }

    public boolean hasSearchCriteria() {
        return searchCriteria != null;
    }
}
