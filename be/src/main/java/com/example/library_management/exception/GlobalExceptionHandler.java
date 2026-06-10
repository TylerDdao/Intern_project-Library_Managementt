package com.example.library_management.exception;

import com.example.library_management.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException e) {
        log.error("Exception code 500: {}", e.getMessage());
        return ResponseEntity.status(500)
                .body(ApiResponse.error("500", e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleAuth(AuthException e) {
        log.warn("Exception code 401: {}", e.getMessage());
        return ResponseEntity.status(401)
                .body(ApiResponse.error("401", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Exception code 403: {}", e.getMessage());
        String message = messageSource.getMessage("error.access.denied", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(403)
                .body(ApiResponse.error("403", message));
    }
}