package com.example.library_management.dto.response;

import com.example.library_management.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String address = null;
    private String email = null;
    private LocalDateTime createdAt = null;
    private LocalDateTime updatedAt = null;
}