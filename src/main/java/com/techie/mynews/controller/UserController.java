package com.techie.mynews.controller;

import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.SourceResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.service.UserService;
import com.techie.mynews.utils.annotation.CurrentUserEmail;
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
    public ResponseEntity<CheckUserResponse> check(@CurrentUserEmail String email) {
        return ResponseEntity.ok().body(userService.checkIfUserExists(email));
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> create(@CurrentUserEmail String email) {
        return ResponseEntity.ok().body(userService.create(email));
    }

    @PostMapping("/sources")
    public ResponseEntity<List<UserSourceResponse>> saveSources(@RequestBody UserSourceRequest userSourceRequest,@CurrentUserEmail String email) {
        return ResponseEntity.ok(userService.saveSources(userSourceRequest,email));
    }

    @GetMapping("/sources")
    public ResponseEntity<List<SourceResponse>> fetchSources(@CurrentUserEmail String email) {
        return ResponseEntity.ok(userService.getSelectedSourcesByUser(email));
    }
}
