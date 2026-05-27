package com.example.library_management.dto;

import lombok.Data;

@Data
public class AccountUpdateRequest {
    private String username;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String address;
    private String email;
}