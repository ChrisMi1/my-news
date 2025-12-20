package com.techie.mynews.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceDto {
    @JsonProperty("id")
    private String externalId;
    private String name;
}
