package com.example.library_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorityResponse {
    private String role;
    private List<String> features;
    private String operation;
}
