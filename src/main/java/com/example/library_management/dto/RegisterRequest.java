package com.example.library_management.dto;

import lombok.Data;

// RegisterRequest.java
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
}
