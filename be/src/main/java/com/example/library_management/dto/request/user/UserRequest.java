package com.example.library_management.dto.request.user;

import com.example.library_management.dto.request.RoleRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    private Long id = null;
    private String username;
    private Long role;
    private String phoneNumber;
    private String fullName;
    private String address = null;
    private String email;
    private String password = null;
}