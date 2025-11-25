package com.techie.mynews.commons.datatable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Accessors(chain = true)
public class SearchResponse<C extends SearchCriteria> {
    private Object content;
    private Integer pageContentSize;
    private Integer pageTotalContentSize;
    private Integer pageNumber;
    private Long totalElements;
    private Integer totalPages;
    private C searchCriteria;
    private List<DatatableSortCriteria> sortCriteria = new ArrayList<>();

}
