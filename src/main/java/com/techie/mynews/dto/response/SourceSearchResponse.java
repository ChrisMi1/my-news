package com.techie.mynews.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SourceSearchResponse {
    private Integer id;
    private String externalId;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;
}
