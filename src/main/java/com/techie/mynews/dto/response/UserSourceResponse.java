package com.techie.mynews.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSourceResponse {
    private String action;
    private String id;
    private UserResponse user;
    private SourceResponse source;
}
