package com.techie.mynews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    @GetMapping("/")
    public String hello() {
        return "hello";
    }

//    @PostMapping("/preferences")
//    public UserResponse savePreferences(@RequestBody UserRequest userRequest) {
//
//
//    }
}
