package com.techie.mynews.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UserSourceRequest {
    private String email;
    private List<Integer> sourcesIds;
}
