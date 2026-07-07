package com.example.library_management.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class SmsRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{6,14}$", message = "Phone number must be in E.164 format, e.g. +84901234567")
    private String toNumber;

    @NotBlank(message = "Message is required")
    @Size(max = 1600, message = "Message too long")
    private String message;
}