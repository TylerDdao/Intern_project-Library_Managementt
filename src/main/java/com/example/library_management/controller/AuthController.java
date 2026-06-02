package com.example.library_management.controller;

import com.example.library_management.dto.*;
import com.example.library_management.dto.response.AccountUpdateResponse;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.LoginResponse;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // remove "Bearer "
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<AccountUpdateResponse>> updateUser(@RequestBody AccountUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.updateAccount(request)));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<UserResponse>> checkUser() {
        UserResponse user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}