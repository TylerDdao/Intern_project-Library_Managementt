package com.example.library_management.controller;

import com.example.library_management.service.AuthService;
import com.example.library_management.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private AuthService authService;

    @Autowired
    private ManagerService managerService;

    @GetMapping("/book")
    public ResponseEntity<?> getManager() {
        return ResponseEntity.ok("This is /GET book endpoint for manager");
    }

    @GetMapping("/users")
    public  ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(managerService.getUsers(page));
    }
}