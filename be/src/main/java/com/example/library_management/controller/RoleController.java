package com.example.library_management.controller;

import com.example.library_management.dto.request.role.AuthorityRequest;
import com.example.library_management.dto.request.role.RoleRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.auth.AuthorityResponse;
import com.example.library_management.dto.response.role.RoleResponse;
import com.example.library_management.service.role.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    AuthorityService authorityService;

    @Autowired
    CreateRoleService createRoleService;

    @Autowired
    UpdateRoleService updateRoleService;

    @Autowired
    GetRoleService getRoleService;

    @Autowired
    DeleteRoleService deleteRoleService;

    @PreAuthorize("@securityService.hasAccess('ASSIGN_FEATURE')")
    @PatchMapping("/assign-feature")
    public ResponseEntity<ApiResponse<AuthorityResponse>> assignFeature(@RequestBody AuthorityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authorityService.assignAuthority(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UNASSIGN_FEATURE')")
    @PatchMapping("/unassign-feature")
    public ResponseEntity<ApiResponse<AuthorityResponse>> unassignFeature(@RequestBody AuthorityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authorityService.unassignAuthority(request)));
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_ROLE')")
    @PostMapping()
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(createRoleService.createRole(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_ROLE')")
    @PatchMapping()
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @RequestBody RoleRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(updateRoleService.updateRole(request)));
    }

    @PreAuthorize("@securityService.hasAccess('GET_ROLE')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<RoleResponse>>> getRoles(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ResponseEntity.ok(ApiResponse.success(getRoleService.getRole(page, limit, sortBy, sortDir, name)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_ROLE')")
    @DeleteMapping()
    public ResponseEntity<ApiResponse<String>> deleteRole(@RequestParam Long id){
        return ResponseEntity.ok(ApiResponse.success(deleteRoleService.deleteRole(id)));
    }
}
