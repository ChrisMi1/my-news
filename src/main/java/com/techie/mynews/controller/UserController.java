package com.techie.mynews.controller;

import com.techie.mynews.dto.request.UserRequest;
import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/check")
    public ResponseEntity<CheckUserResponse> check(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.checkIfUserExists(userRequest.email()));
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.create(userRequest.email()));
    }

    @PostMapping("/sources")
    public ResponseEntity<List<UserSourceResponse>> saveSources(@RequestBody UserSourceRequest userSourceRequest) {
        return ResponseEntity.ok(userService.saveSources(userSourceRequest));
    }
}
