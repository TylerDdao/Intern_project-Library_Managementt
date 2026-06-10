package com.example.library_management.dto.response;

import com.example.library_management.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

// LoginResponse.java
@Data
@AllArgsConstructor
public class LoginResponse {
    private UserResponse user;
    private String token;

    public LoginResponse(User user, String token){
        this.user = new UserResponse(user);
        this.token =token;
    }
}