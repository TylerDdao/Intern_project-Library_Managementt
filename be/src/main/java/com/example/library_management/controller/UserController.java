package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.request.UserRequest;
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
@RequestMapping("/users")
public class UserController {
    @Autowired
    GetUserService getUserService;

    @Autowired
    UpdateUserService updateUserService;

    @Autowired
    DeleteUserService deleteUserService;

    @PreAuthorize("@securityService.hasAccess('GET_USER')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                ApiResponse.success(getUserService.getUsers(page, limit, sortBy, sortDir, username, fullName, role))
        );
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER')")
    @PatchMapping()
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByUsername(
            @RequestBody UserRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUser(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_BOOK')")
    @DeleteMapping()
    public  ResponseEntity<ApiResponse<String>> deleteUserByUsername(@RequestBody UserRequest request){
        return ResponseEntity.ok(ApiResponse.success(deleteUserService.deleteUser(request)));
    }
}
