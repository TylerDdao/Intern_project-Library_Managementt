package com.example.library_management.controller;

import com.example.library_management.dto.request.LoginRequest;
import com.example.library_management.dto.request.auth.RegisterRequest;
import com.example.library_management.dto.request.auth.VerificationRequest;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.LoginResponse;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.dto.response.auth.VerificationResponse;
import com.example.library_management.service.auth.AuthService;
import com.example.library_management.service.auth.VerificationService;
import com.example.library_management.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationEmail(
            @RequestBody UserRequest request) {
        String message = verificationService.sendVerificationEmail(request);
        return switch (message) {
            case "verification.Code.is.sent" -> ResponseEntity.ok(ApiResponse.success(messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
            case "error.Code.is.already.sent" -> ResponseEntity.badRequest().body(ApiResponse.error("CODE-ALREADY-SENT", messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
            case "error.Email.has.been.used" -> ResponseEntity.badRequest().body(ApiResponse.error("EMAIL-IN-USE", messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
            default -> ResponseEntity.internalServerError().body(ApiResponse.error("SERVER-ERROR", messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
        };
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyEmail(
            @RequestBody VerificationRequest request
    ){
        VerificationResponse response = new VerificationResponse(request.getEmail(), verificationService.verifyCode(request.getEmail(), request.getCode()));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) throws MessagingException {
        return ResponseEntity.status(201).body(ApiResponse.success(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        System.out.println("Login called");
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER')")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.updateAccount(request)));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<UserResponse>> checkUser() {
        UserResponse user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}