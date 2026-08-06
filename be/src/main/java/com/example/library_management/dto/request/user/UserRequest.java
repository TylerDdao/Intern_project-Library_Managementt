package com.example.library_management.dto.request.user;

import com.example.library_management.model.User;
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

    public UserRequest(){

    }

    public UserRequest(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().getId();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.address = user.getAddress();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}