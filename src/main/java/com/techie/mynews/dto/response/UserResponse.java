package com.techie.mynews.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
@Getter
@Setter
public class UserResponse {
    private String id;
    private String email;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}