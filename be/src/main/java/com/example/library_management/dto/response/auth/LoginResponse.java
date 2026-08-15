package com.example.library_management.dto.response.auth;

import com.example.library_management.dto.response.user.UserResponse;
import com.example.library_management.model.Feature;
import com.example.library_management.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// LoginResponse.java
@Data
@AllArgsConstructor
public class LoginResponse {
    private UserResponse user;
    private List<String> authorities = new ArrayList<>();
    private String token;

    public LoginResponse(User user, String token, List<Feature> authorities) {
        this.user = new UserResponse(user);
        this.token = token;
        authorities.forEach(feature -> {
            this.authorities.add(feature.getName());
        });
    }
}