package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.FeatureResponse;
import com.example.library_management.dto.response.RoleResponse;
import com.example.library_management.service.feature.GetFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/features")
public class FeatureController {

    @Autowired
    GetFeatureService getFeatureService;

    @PreAuthorize("@securityService.hasAccess('GET_FEATURE')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<FeatureResponse>>> getFeatures(
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ResponseEntity.ok(ApiResponse.success(getFeatureService.getFeature(page, limit, sortBy, sortDir, role)));
    }
}
