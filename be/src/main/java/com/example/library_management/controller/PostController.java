package com.example.library_management.controller;

import com.example.library_management.dto.request.post.PostRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.post.PostResponse;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.post.CreatePostService;
import com.example.library_management.service.post.DeletePostService;
import com.example.library_management.service.post.GetPostService;
import com.example.library_management.service.post.UpdatePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private GetPostService getPostService;

    @Autowired
    private DeletePostService deletePostService;

    @Autowired
    private UpdatePostService updatePostService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreatePostService createPostService;

//    @PreAuthorize("@securityService.hasAccess('GET_POST')")
//    @GetMapping()
//    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByBookId(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int limit,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir,
//            @RequestParam(required = true) int bookId) {
//        return ResponseEntity.ok(
//                ApiResponse.success(getPostService.getPostsByBookId(page, limit, sortBy, sortDir, bookId))
//        );
//    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_POST')")
    @PatchMapping()
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @RequestBody PostRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(updatePostService.updatePost(request)));
    }

    @PreAuthorize("@securityService.hasAccess('GET_POST')")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId){
        return ResponseEntity.ok(ApiResponse.success((getPostService.getPostById(postId))));
    }

    @PreAuthorize("@securityService.hasAccess('GET_POST')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String searchQuery) {
        return ResponseEntity.ok(
                ApiResponse.success(getPostService.getPosts(page, limit, sortBy, sortDir, searchQuery, bookId))
        );
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> toggleLike(@PathVariable Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String result = updatePostService.toggleLike(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PreAuthorize("@securityService.hasAccess('GET_POST')")
    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(
                ApiResponse.success(getPostService.getMyPosts(page, limit, sortBy, sortDir))
        );
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_POST')")
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @RequestParam() Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(deletePostService.deletePost(id))
        );
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_POST')")
    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody PostRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(createPostService.createPost(request))
        );
    }
}
