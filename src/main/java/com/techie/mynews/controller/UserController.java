package com.techie.mynews.controller;

import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;
    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @PostMapping("/sources")
    public ResponseEntity<List<UserSourceResponse>> saveSources(@AuthenticationPrincipal Jwt jwt, @RequestBody UserSourceRequest userSourceRequest) {
        return ResponseEntity.ok(userService.saveSources(jwt, userSourceRequest));
    }
}
