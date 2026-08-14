package com.example.library_management.controller;

import com.example.library_management.dto.request.comment.CommentRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.comment.CommentResponse;
import com.example.library_management.service.comment.CreateCommentService;
import com.example.library_management.service.comment.DeleteCommentService;
import com.example.library_management.service.comment.GetCommentService;
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
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestBody CommentRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(createCommentService.createComment(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_COMMENT')")
    @DeleteMapping()
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @RequestParam() Long id
    ){
        return ResponseEntity.ok(ApiResponse.success(deleteCommentService.deleteComment(id)));
    }
}
