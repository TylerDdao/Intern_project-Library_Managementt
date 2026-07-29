package com.example.library_management.controller;

import com.example.library_management.dto.request.LoginRequest;
import com.example.library_management.dto.request.RegisterRequest;
import com.example.library_management.dto.request.auth.VerificationRequest;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.LoginResponse;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.dto.response.auth.VerificationResponse;
import com.example.library_management.service.Auth.AuthService;
import com.example.library_management.service.Auth.VerificationService;
import com.example.library_management.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
    public ResponseEntity<ApiResponse<String>> SendVerificationEmail(
            @RequestBody UserRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String lang
            ) {
        Locale locale = (lang != null) ? Locale.forLanguageTag(lang) : Locale.ENGLISH;
        String message = verificationService.sendVerificationEmail(request, locale);
        if(message.equals("verification.Code.is.sent")){
            return ResponseEntity.ok(ApiResponse.success(messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
        }
        else if(message.equals("error.Code.is.already.sent")){
          return ResponseEntity.ok(ApiResponse.error("CODE-ALREADY-SENT", messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
        }
        else if (message.equals("error.Email.has.been.used")) {
            return ResponseEntity.ok(ApiResponse.error("EMAIL-IN-USE", messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
        } else{
            return ResponseEntity.internalServerError().body(ApiResponse.error("SERVER-ERROR", messageSource.getMessage(message, null, LocaleContextHolder.getLocale())));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationResponse>> BerifyEmail(
            @RequestBody VerificationRequest request
    ){
        if (verificationService.verifyCode(request.getEmail(), request.getCode())){
            VerificationResponse response = new VerificationResponse(request.getEmail(), true);
            return ResponseEntity.ok(ApiResponse.success(response));
        }
        else{
            VerificationResponse response = new VerificationResponse(request.getEmail(), false);
            return ResponseEntity.ok(ApiResponse.success(response));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
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