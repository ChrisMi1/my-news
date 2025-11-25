package com.techie.mynews.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


public record UserRequest(
        String categories,
        String sources
) {

}
