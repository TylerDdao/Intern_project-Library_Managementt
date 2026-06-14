package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.dto.response.PostResponse;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.post.GetPostService;
import com.example.library_management.service.post.UpdatePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    private GetPostService getPostService;

    @Autowired
    private UpdatePostService updatePostService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("@securityService.hasAccess('GET_POST')")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String searchQuery) {
        return ResponseEntity.ok(
                ApiResponse.success(getPostService.getPosts(page, limit, sortBy, sortDir, searchQuery))
        );
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_POST')")
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<ApiResponse<String>> toggleLike(@PathVariable Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String result = updatePostService.toggleLike(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PreAuthorize("@securityService.hasAccess('GET_POST')")
    @GetMapping("/posts/my-posts")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = true) Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(getPostService.getPostsByUserId(page, limit, sortBy, sortDir, userId))
        );
    }
}
