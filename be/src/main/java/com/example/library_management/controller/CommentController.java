package com.example.library_management.controller;

import com.example.library_management.dto.request.comment.CommentRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.comment.CommentResponse;
import com.example.library_management.service.comment.CreateCommentService;
import com.example.library_management.service.comment.DeleteCommentService;
import com.example.library_management.service.comment.GetCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private GetCommentService getCommentService;
    @Autowired
    private CreateCommentService createCommentService;
    @Autowired
    private DeleteCommentService deleteCommentService;

    @PreAuthorize("@securityService.hasAccess('GET_COMMENT')")
    @GetMapping()
    @Operation(summary = "Get comments", description = "Get all comments of a post by post ID, require 'GET_COMMENT' feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "Post not found",
                                            description = "Post can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "POST-NOT-FOUND",
                                                    "message": "Post not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = true) Long postId) {
        return ResponseEntity.ok(
                ApiResponse.success(getCommentService.getCommentByPostId(page, limit, sortBy, sortDir, postId))
        );
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_COMMENT')")
    @PostMapping()
    @Operation(summary = "Add comment", description = "Add a comment in a post, require 'CREATE_COMMENT' feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "Post not found",
                                            description = "Post can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "POST-NOT-FOUND",
                                                    "message": "Post not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestBody CommentRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(createCommentService.createComment(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_COMMENT')")
    @DeleteMapping()
    @Operation(summary = "Delete comment", description = "Delete a comment, require 'DELETE_COMMENT' feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                                            name = "Comment not found",
                                            description = "Comment can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "COMMENT-NOT-FOUND",
                                                    "message": "Comment not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @RequestParam() Long id
    ){
        return ResponseEntity.ok(ApiResponse.success(deleteCommentService.deleteComment(id)));
    }
}
