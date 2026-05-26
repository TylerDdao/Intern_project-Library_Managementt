package com.example.library_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String address = null;
    private String email = null;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}