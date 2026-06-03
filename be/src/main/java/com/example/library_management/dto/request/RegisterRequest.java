package com.example.library_management.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

// RegisterRequest.java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    private String username;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String address = null;
    private String email = null;
    private String password;
}
