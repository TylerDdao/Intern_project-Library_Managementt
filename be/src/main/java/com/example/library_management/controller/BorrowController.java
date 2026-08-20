package com.example.library_management.controller;

import com.example.library_management.dto.request.borrow.BorrowRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.borrow.BorrowResponse;
import com.example.library_management.service.borrow.CreateBorrowService;
import com.example.library_management.service.borrow.GetBorrowService;
import com.example.library_management.service.borrow.UpdateBorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/borrows")
@Tag(name = "Borrows", description = "Borrows management endpoints" )
public class BorrowController {
    @Autowired
    CreateBorrowService createBorrowService;
    @Autowired
    GetBorrowService getBorrowService;

    @Autowired
    UpdateBorrowService updateBorrowService;


    @PreAuthorize("@securityService.hasAccess('CREATE_BORROW')")
    @PostMapping()
    @Operation(summary = "Add borrow", description = "Add new borrow, require 'CREATE_BORROW' feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                                                    {"code": "POLICY-NOT-FOUND",
                                                    "message": "Policy not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Book not found",
                                            description = "Book can't be found by its ID",
                                            value = """
                                                    {"code": "BOOK-NOT-FOUND",
                                                    "message": "Book not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
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
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Borrow is already existed",
                                            description = "A borrow with he same user ID and book ID is already existed",
                                            value = """
                                                    {"code": "BORROW-ALREADY-EXISTED",
                                                    "message": "Borrow is already existed"
                                                    "data": null,
                                                    "timestamp" "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<BorrowResponse>> createBorrow(@RequestBody BorrowRequest request){
        return ResponseEntity.ok(ApiResponse.success(createBorrowService.createBorrow(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_BORROW')")
    @PatchMapping("/return")
    @Operation(summary = "Return borrow", description = "Change the borrow's status to 'Inactive', require 'UPDATE_BORROW' feature, default users shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "Borrow not found",
                                            description = "Borrow can't be found by its ID",
                                            value = """
                                                    {"code": "BORROW-NOT-FOUND",
                                                    "message": "Borrow not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Book not found",
                                            description = "Book can't be found by its ID",
                                            value = """
                                                    {"code": "BOOK-NOT-FOUND",
                                                    "message": "Book not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
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
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Borrow is not active",
                                            description = "Borrow is not active (already returned)",
                                            value = """
                                                    {"code": "BORROW-IS-NOT-ACTIVE",
                                                    "message": "Borrow is already returned"
                                                    "data": null,
                                                    "timestamp" "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<BorrowResponse>> returnBorrow(
            @RequestBody BorrowRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(updateBorrowService.returnBorrow(request)));
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW')")
    @GetMapping("/my-borrows")
    @Operation(summary = "Get borrows of an user", description = "Get borrows that are associate with username and can be filtered by book ID, require 'GET_BORROW' feature", security = @SecurityRequirement(name = "BearerAuth"))
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
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getMyBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "true") boolean isActive,
            @RequestParam(required = false) Long bookId
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getMyBorrows(page, limit, sortBy, sortDir, isActive, bookId))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW')")
    @GetMapping("/borrows-count/genre")
    @Operation(summary = "Get borrow count by genre", description = "Get the number of borrow in each book genre, require 'GET_BORROW' feature", security = @SecurityRequirement(name = "BearerAuth"))
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
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBorrowsCountsByGenre() {
        return ResponseEntity.ok(ApiResponse.success(getBorrowService.getBorrowCountsByGenre()));
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW_MULTI')")
    @GetMapping("/{status}")
    @Operation(summary = "Get borrows by status of all user", description = "Administrator can get borrows in each status (On going, Late, Returned) of all users, require 'GET_BORROW_MULTI' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getBorrowsByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @PathVariable String status
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrowsByStatus(page, limit, sortBy, sortDir, status))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW_MULTI')")
    @GetMapping()
    @Operation(summary = "Get borrow of all user", description = "Administrator can see the borrow's information of all users, require 'GET_BORROW_MULTI' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getBorrowsBySearchQuery(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrows(page, limit, sortBy, sortDir, query))
        );
    }
}
