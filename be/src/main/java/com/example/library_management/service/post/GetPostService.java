package com.example.library_management.service.post;

import com.example.library_management.dto.response.post.PostResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Post;
import com.example.library_management.model.User;
import com.example.library_management.repository.PostLikeRepository;
import com.example.library_management.repository.PostRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetPostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public Page<PostResponse> getPosts(int page, int limit, String sortBy, String sortDir, String searchQuery, Long bookId) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Post> posts;

        if (bookId == null) {
            posts = searchQuery != null && !searchQuery.isBlank()
                    ? postRepository.findBySearchQuery(searchQuery, pageable)
                    : postRepository.findAll(pageable);
        } else {
            posts = postRepository.findByBookId(bookId, pageable);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow();
        List<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUser(currentUser);

        return posts.map(post -> new PostResponse(post, likedPostIds.contains(post.getId()), post.getCreatedBy().equals(username)));
    }

    public Page<PostResponse> getMyPosts(int page, int limit, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<Post> posts = postRepository.findByCreatedBy(username, pageable);
        List<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUserUsername(username);
        return posts.map(post -> new PostResponse(post, likedPostIds.contains(post.getId()), post.getCreatedBy().equals(username)));
    }

    public Page<PostResponse> getPostsByUserId(int page, int limit, String sortBy, String sortDir, Long userId){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        User user = userRepository.findByUsernameAndIsDeletedFalse(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new RuntimeException(messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));
        Page<Post> posts = postRepository.findByCreatedBy(user.getUsername(), pageable);
        List<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUser(user);
        return posts.map(post -> new PostResponse(post, likedPostIds.contains(post.getId()), post.getCreatedBy().equals(user.getUsername())));
    }

    public PostResponse getPostById(Long postId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;

        Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "POST-NOT-FOUND", messageSource.getMessage("error.post.not.found", null, LocaleContextHolder.getLocale())));

        boolean isLiked = username != null && postLikeRepository.findByPostIdAndUserUsername(postId, username).isPresent();
        boolean isOwner = Objects.equals(post.getCreatedBy(), username);

        return new PostResponse(post, isLiked, isOwner);
    }
}
