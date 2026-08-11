package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.User;
import com.example.library_management.service.user.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private GetUserService getUserService;

    @Autowired
    private UpdateUserService updateUserService;

    @Autowired
    private DeleteUserService deleteUserService;

    @Autowired
    private CreateUserService createUserService;

    @PreAuthorize("@securityService.hasAccess('CREATE_USER')")
    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody UserRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(createUserService.createUser(request)));
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(
            @RequestParam() String username
    ){
        return ResponseEntity.ok(ApiResponse.success(getUserService.checkUsername(username)));
    }

    @PreAuthorize("@securityService.hasAccess('GET_USER')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                ApiResponse.success(getUserService.getUsers(page, limit, sortBy, sortDir, role, query))
        );
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER')")
    @PatchMapping("update-me")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByUsername(
            @RequestBody UserRequest request) throws MessagingException {
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUserSelf(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER_MULTI')")
    @PatchMapping()
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestBody UserRequest request) throws MessagingException {
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUser(request)));
    }

    // TODO: Seperate update user multi

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER_ROLE')")
    @PatchMapping("/update-role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoleByUsername(
            @RequestBody UserRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUserRole(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_USER_MULTI')")
    @DeleteMapping()
    public  ResponseEntity<ApiResponse<String>> deleteUserByUsername(@RequestParam Long id){
        return ResponseEntity.ok(ApiResponse.success(deleteUserService.deleteUser(id)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_USER')")
    @DeleteMapping("/delete-me")
    public  ResponseEntity<ApiResponse<String>> deleteMyUser(){
        return ResponseEntity.ok(ApiResponse.success(deleteUserService.deleteUser()));
    }
}
