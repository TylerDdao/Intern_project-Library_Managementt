package com.example.library_management.dto.request.auth;

import lombok.Data;

// LoginRequest.java
@Data
public class LoginRequest {
    private String username;
    private String password;
    private String turnstileToken;
}