package com.example.library_management.exception;

import com.example.library_management.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException e) {
        return ResponseEntity.status(500)
                .body(ApiResponse.error("500", e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleAuth(AuthException e) {
        return ResponseEntity.status(401)
                .body(ApiResponse.error("401", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(403)
                .body(ApiResponse.error("403", "Access denied"));
    }
}