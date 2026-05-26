package com.example.library_management.controller;

import com.example.library_management.dto.LoginRequest;
import com.example.library_management.dto.LoginResponse;
import com.example.library_management.dto.RegisterRequest;
import com.example.library_management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthService authService;

    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping("/book")
    public ResponseEntity<?> getBook() {
        return ResponseEntity.ok("This is /GET book endpoint");
    }
}