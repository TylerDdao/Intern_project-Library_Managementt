package com.example.library_management.service.post;

import com.example.library_management.dto.response.PostResponse;
import com.example.library_management.model.Book;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Page<PostResponse> getPosts(int page, int limit, String sortBy, String sortDir, String searchQuery) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Post> posts = searchQuery != null && !searchQuery.isBlank()
                ? postRepository.findBySearchQuery(searchQuery, pageable)
                : postRepository.findAll(pageable);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        List<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUser(currentUser);

        return posts.map(post -> new PostResponse(post, likedPostIds.contains(post.getId()), post.getCreatedBy().equals(username)));
    }

    public Page<PostResponse> getPostsByUserId(int page, int limit, String sortBy, String sortDir, Long userId){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new RuntimeException(messageSource.getMessage("user.user.id.not.found", null, LocaleContextHolder.getLocale())));
        Page<Post> posts = postRepository.findByCreatedBy(user.getUsername(), pageable);
        List<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUser(user);
        return posts.map(post -> new PostResponse(post, likedPostIds.contains(post.getId()), post.getCreatedBy().equals(user.getUsername())));
    }
}
