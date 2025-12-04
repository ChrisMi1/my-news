package com.techie.mynews.dto.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceResponse {
    private Integer id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;
}
