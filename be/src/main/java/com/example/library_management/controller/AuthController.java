package com.example.library_management.controller;

import com.example.library_management.dto.request.auth.LoginRequest;
import com.example.library_management.dto.request.auth.RegisterRequest;
import com.example.library_management.dto.request.auth.VerificationRequest;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.auth.LoginResponse;
import com.example.library_management.dto.response.user.UserResponse;
import com.example.library_management.dto.response.auth.VerificationResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.exception.AuthException;
import com.example.library_management.service.auth.AuthService;
import com.example.library_management.service.auth.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management endpoints" )
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VerificationService verificationService;

    @PatchMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset user's password")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(
            @RequestBody UserRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(authService.resetPassword(request)));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Reset password", description = "Verify code used to reset password")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyResetPasswordCode(
            @RequestParam String code,
            @RequestParam String email
    ){
        return ResponseEntity.ok(ApiResponse.success(verificationService.verifyResetPasswordCode(email, code)));
    }

    @GetMapping("/forgot-password")
    @Operation(summary = "Reset password", description = "Send an email with a link to reset user's password")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "User does not exist",
                                            value = """
                                                    {
                                                        "code": "USER-NOT-FOUND",
                                                        "message": "User not found",
                                                        "data": null,
                                                        "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Capcha failed",
                                            summary = "Failed to verify capcha",
                                            value = """
                                                    {
                                                        "code": "401",
                                                        "message": "Failed to verify capcha",
                                                        "data": null,
                                                        "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<String>> sendResetPasswordEmail(
            @RequestParam String email,
            @RequestParam String capcha
    ) {
        String message = verificationService.sendResetPasswordEmail(email, capcha);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/send-verification-code")
    @Operation(summary = "Verify email", description = "Send an email with a code to verify user's email address")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Email has been used",
                                            summary = "Email has been used for another user",
                                            value = """
                                                    {
                                                        "code": "EMAIL-IS-USED",
                                                        "message": "Email has been used",
                                                        "data": null,
                                                        "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<String>> sendVerificationEmail(
            @RequestBody UserRequest request) {
        String message = verificationService.sendVerificationEmail(request);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify email", description = "Verify the email verification code")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
    })
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyEmail(
            @RequestBody VerificationRequest request
    ){
        VerificationResponse response = new VerificationResponse(request.getEmail(), verificationService.verifyCode(request.getEmail(), request.getCode()));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register new user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Username is taken",
                                            summary = "Username is taken by another user",
                                            value = """
                                                    {
                                                        "code": "USERNAME-TAKEN",
                                                        "message": "Username is taken",
                                                        "data": null,
                                                        "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RuntimeException.class)
                    )
            ),
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(authService.register(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login user and return JwT token if credential is correct")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Incorrect credential",
                                            summary = "Credential provided is incorrect",
                                            value = """
                                                    {
                                                        "code": "401",
                                                        "message": "Invalid username or password",
                                                        "data": null,
                                                        "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )

            )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify-password")
    @Operation(summary = "Verify password", description = "Verify if user's password is correct")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Incorrect credential",
                                            summary = "Credential provided is incorrect",
                                            value = """
                                                    {
                                                        "code": "401",
                                                        "message": "Invalid username or password",
                                                        "data": null,
                                                        "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyPassword(@RequestBody LoginRequest request){
        return ResponseEntity.ok((ApiResponse.success(authService.verifyPassword(request))));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate user's JWT token", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER')")
    @PatchMapping("/update")
    @Operation(summary = "Update user", description = "Update user's information by username, require 'UPDATE_USER' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.updateAccount(request)));
    }

    @GetMapping("/check")
    @Operation(summary = "Check user", description = "Check user's information", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> checkUser() {
        UserResponse user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}