package com.techie.mynews.dto.request;

import com.techie.mynews.commons.datatable.SearchCriteria;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SourceCriteriaRequest implements SearchCriteria {
    private String name;
    private String country;
}
