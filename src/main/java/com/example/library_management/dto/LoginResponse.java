package com.example.library_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// LoginResponse.java
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
}