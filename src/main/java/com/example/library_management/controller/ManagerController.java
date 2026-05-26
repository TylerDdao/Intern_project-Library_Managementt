package com.example.library_management.controller;

import com.example.library_management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private AuthService authService;

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping("/book")
    public ResponseEntity<?> getManager() {
        return ResponseEntity.ok("This is /GET book endpoint for manager");
    }
}