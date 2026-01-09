package com.techie.mynews.controller;

import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.SourceResponse;
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

    @GetMapping("/check")
    public ResponseEntity<CheckUserResponse> check() {
        return ResponseEntity.ok().body(userService.checkIfUserExists());
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> create() {
        return ResponseEntity.ok().body(userService.create());
    }

    @PostMapping("/sources")
    public ResponseEntity<List<UserSourceResponse>> saveSources(@RequestBody UserSourceRequest userSourceRequest) {
        return ResponseEntity.ok(userService.saveSources(userSourceRequest));
    }

    @GetMapping("/sources")
    public ResponseEntity<List<SourceResponse>> fetchSources() {
        return ResponseEntity.ok(userService.getSelectedSourcesByUser());
    }
}
