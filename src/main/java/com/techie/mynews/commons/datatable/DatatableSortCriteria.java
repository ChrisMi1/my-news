package com.techie.mynews.commons.datatable;

public class DatatableSortCriteria {
    public enum SortTypeEnum {
        ASC,
        DESC
    }
    private String field;
    private SortTypeEnum type;
}
