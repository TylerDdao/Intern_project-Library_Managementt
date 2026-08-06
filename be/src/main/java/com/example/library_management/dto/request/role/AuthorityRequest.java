package com.example.library_management.dto.request.role;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorityRequest {
    private Long id;
    private List<String> features;
}
