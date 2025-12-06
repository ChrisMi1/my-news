package com.techie.mynews.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckUserResponse {
    private Boolean exists;
    private UserResponse userResponse;
}
