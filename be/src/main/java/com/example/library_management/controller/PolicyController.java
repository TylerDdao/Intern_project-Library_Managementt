package com.example.library_management.controller;

import com.example.library_management.dto.request.policy.PolicyRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.policy.PolicyResponse;
import com.example.library_management.service.policy.GetPolicyService;
import com.example.library_management.service.policy.UpdatePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policies")
public class PolicyController {
    @Autowired
    private GetPolicyService getPolicyService;

    @Autowired
    private UpdatePolicyService updatePolicyService;

    @PreAuthorize("@securityService.hasAccess('GET_POLICY')")
    @GetMapping()
    public ResponseEntity<ApiResponse<PolicyResponse>> getPolicy(
            @RequestParam() String key
    ){
        return ResponseEntity.ok(ApiResponse.success(getPolicyService.getPolicy(key)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_POLICY')")
    @PatchMapping()
    public ResponseEntity<ApiResponse<PolicyResponse>> updatePolicy(
            @RequestBody()PolicyRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(updatePolicyService.updatePolicy(request)));
    }
}
