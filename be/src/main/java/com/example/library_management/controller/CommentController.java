package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.CommentResponse;
import com.example.library_management.dto.response.PostResponse;
import com.example.library_management.service.comment.GetCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {
    @Autowired
    private GetCommentService getCommentService;
    @PreAuthorize("@securityService.hasAccess('GET_COMMENT')")
    @GetMapping("/comments")
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
}
