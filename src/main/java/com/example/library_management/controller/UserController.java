package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.UserRequest;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.service.user.DeleteUserService;
import com.example.library_management.service.user.GetUserService;
import com.example.library_management.service.user.UpdateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    GetUserService getUserService;

    @Autowired
    UpdateUserService updateUserService;

    @Autowired
    DeleteUserService deleteUserService;

    @PreAuthorize("hasAuthority('GET_USERS')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String username) {

        if (username != null && !username.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.success(getUserService.getUserByUsername(page, username))
            );
        }
        return ResponseEntity.ok(
                ApiResponse.success(getUserService.getUsers(page))
        );
    }

    @PreAuthorize("hasAuthority('GET_USER')")
    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUserByUsername(@PathVariable String username, @RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(ApiResponse.success(getUserService.getUserByUsername(page, username)));
    }

    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByUsername(@RequestBody UserRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUser(request)));
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    @DeleteMapping("/user")
    public  ResponseEntity<ApiResponse<String>> deleteUserByUsername(@RequestBody UserRequest request){
        return ResponseEntity.ok(ApiResponse.success(deleteUserService.deleteUser(request)));
    }
}
