package com.example.library_management.controller;

import com.example.library_management.dto.*;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.status(201).body(message);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // remove "Bearer "
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody AccountUpdateRequest request) {
        return ResponseEntity.ok(authService.updateAccount(request));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkUser() {
        UserResponse user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}