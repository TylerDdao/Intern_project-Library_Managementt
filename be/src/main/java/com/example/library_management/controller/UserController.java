package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.user.UserResponse;
import com.example.library_management.service.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Users management endpoints" )
public class UserController {
    @Autowired
    private GetUserService getUserService;

    @Autowired
    private UpdateUserService updateUserService;

    @Autowired
    private DeleteUserService deleteUserService;

    @Autowired
    private CreateUserService createUserService;

    @PreAuthorize("@securityService.hasAccess('CREATE_USER')")
    @PostMapping()
    @Operation(summary = "Create a new user", description = "Administrator can create a new user, difference from register endpoint is this this endpoint allows to set user's role instead of auto assign user default role, require 'CREATE_USER' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "Role not found",
                                            description = "Role can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "ROLE-NOT-FOUND",
                                                    "message": "Role not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody UserRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(createUserService.createUser(request)));
    }

    @GetMapping("/check-username")
    @Operation(summary = "Check username", description = "Check if username is taken by another user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Username available",
                                            description = "Username is available and can be used",
                                            value = """
                                                    {
                                                    "code": "200",
                                                    "message": "Success",
                                                    "data": true,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Username unavailable",
                                            description = "Username is unavailable and can't be used",
                                            value = """
                                                    {
                                                    "code": "200",
                                                    "message": "Success",
                                                    "data": false,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(
            @RequestParam() String username
    ){
        return ResponseEntity.ok(ApiResponse.success(getUserService.checkUsername(username)));
    }

    @PreAuthorize("@securityService.hasAccess('GET_USER_MULTI')")
    @GetMapping()
    @Operation(summary = "Get multiple users", description = "Administrator can get multiple users' information, require 'GET_USER_MULTI' feature, default user shall not be granted this feature",security = @SecurityRequirement(name = "BearerAuth"))
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
    })
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                ApiResponse.success(getUserService.getUsers(page, limit, sortBy, sortDir, role, query))
        );
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER')")
    @PatchMapping("update-me")
    @Operation(summary = "Update user", description = "Update an user by username of the request, require 'UPDATE_USER' feature",security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "User not found",
                                            description = "User can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "USER-NOT-FOUND",
                                                    "message": "User not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Sent email failed",
                                            description = "Server can't send email",
                                            value = """
                                                    {
                                                    "code": "EMAIL-ERROR",
                                                    "message": "Email send failed",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByUsername(
            @RequestBody UserRequest request) throws MessagingException {
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUserSelf(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER_MULTI')")
    @PatchMapping()
    @Operation(summary = "Update multi user", description = "Administrator can update multi users, require 'UPDATE_USER_MULTI' feature, default user shall not be granted this feature",security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "User not found",
                                            description = "User can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "USER-NOT-FOUND",
                                                    "message": "User not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Sent email failed",
                                            description = "Server can't send email",
                                            value = """
                                                    {
                                                    "code": "EMAIL-ERROR",
                                                    "message": "Email send failed",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestBody UserRequest request) throws MessagingException {
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUser(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_USER_ROLE')")
    @PatchMapping("/update-role")
    @Operation(summary = "Update user's role", description = "Administrator can update user's role, require 'UPDATE_USER_ROLE' feature, default user shall not be granted this feature",security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "User not found",
                                            description = "User can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "USER-NOT-FOUND",
                                                    "message": "User not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Role not found",
                                            description = "Role can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "ROLE-NOT-FOUND",
                                                    "message": "Role not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Can't remove last root user",
                                            description = "Can't remove last root user, there msut be at least 1 root user",
                                            value = """
                                                    {
                                                    "code": "ROOT-USER",
                                                    "message": "Cannot remove last root user",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoleByUsername(
            @RequestBody UserRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateUserService.updateUserRole(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_USER_MULTI')")
    @DeleteMapping()
    @Operation(summary = "Delete multi users", description = "Administrator can delete multi user, require 'DELETE_USER_MULTI' feature, default user shall not be granted this feature",security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "User not found",
                                            description = "User can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "USER-NOT-FOUND",
                                                    "message": "User not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Can't delete last root user",
                                            description = "Can't delete last root user, there msut be at least 1 root user",
                                            value = """
                                                    {
                                                    "code": "ROOT-USER",
                                                    "message": "Cannot delete last root user",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Can't delete user with on going borrow(s)",
                                            description = "Can't delete user with on going borrow(s), make sure user returned all borrows",
                                            value = """
                                                    {
                                                    "code": "USER-WITH-BORROWS",
                                                    "message": "Cannot delete user with on going borrow(s)",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public  ResponseEntity<ApiResponse<String>> deleteUserByUsername(@RequestParam Long id){
        return ResponseEntity.ok(ApiResponse.success(deleteUserService.deleteUser(id)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_USER')")
    @DeleteMapping("/delete-me")
    @Operation(summary = "Delete user", description = "Delete an user by username of the request, require 'DELETE_USER' feature",security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "User not found",
                                            description = "User can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "USER-NOT-FOUND",
                                                    "message": "User not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Can't delete last root user",
                                            description = "Can't delete last root user, there msut be at least 1 root user",
                                            value = """
                                                    {
                                                    "code": "ROOT-USER",
                                                    "message": "Cannot delete last root user",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Can't delete user with on going borrow(s)",
                                            description = "Can't delete user with on going borrow(s), make sure user returned all borrows",
                                            value = """
                                                    {
                                                    "code": "USER-WITH-BORROWS",
                                                    "message": "Cannot delete user with on going borrow(s)",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public  ResponseEntity<ApiResponse<String>> deleteMyUser(){
        return ResponseEntity.ok(ApiResponse.success(deleteUserService.deleteUser()));
    }
}
