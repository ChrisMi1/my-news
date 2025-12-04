package com.techie.mynews.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSourceResponse {
    private String id;
    private UserResponse user;
    private SourceResponse source;
}
