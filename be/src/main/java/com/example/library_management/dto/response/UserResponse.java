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
    private RoleResponse role;
    private String phoneNumber;
    private String fullName;
    private String address = null;
    private String email = null;
    private boolean isDeleted = false;

    public UserResponse(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = new RoleResponse(user.getRole());
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.address = user.getAddress();
        this.email = user.getEmail();
        this.isDeleted = user.getIsDeleted();
    }
}