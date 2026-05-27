package com.example.library_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountUpdateResponse {
    private Long id;
    private String username;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String address;
    private String email;
}
