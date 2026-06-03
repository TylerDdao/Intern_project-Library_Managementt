package com.example.library_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorityRequest {
    private String role;
    private List<String> features;
}
