package com.example.library_management.controller;

import com.example.library_management.dto.request.policy.PolicyRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.policy.PolicyResponse;
import com.example.library_management.service.policy.GetPolicyService;
import com.example.library_management.service.policy.UpdatePolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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


    @GetMapping()
    @Operation(summary = "Get policy", description = "Get a policy by key", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
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
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Policy not found",
                                            description = "Policy can't be found by its key",
                                            value = """
                                                    {
                                                    "code": "POLICY-NOT-FOUND",
                                                    "message": "Policy not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<PolicyResponse>> getPolicy(
            @RequestParam() String key
    ){
        return ResponseEntity.ok(ApiResponse.success(getPolicyService.getPolicy(key)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_POLICY')")
    @PatchMapping()
    @Operation(summary = "Update policy", description = "Update a policy by key, require 'UPDATE_POLICY' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Policy not found",
                                            description = "Policy can't be found by its key",
                                            value = """
                                                    {
                                                    "code": "POLICY-NOT-FOUND",
                                                    "message": "Policy not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<PolicyResponse>> updatePolicy(
            @RequestBody()PolicyRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(updatePolicyService.updatePolicy(request)));
    }
}
