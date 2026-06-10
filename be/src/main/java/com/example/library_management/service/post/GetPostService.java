package com.example.library_management.service.post;

import com.example.library_management.dto.response.PostResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Post;
import com.example.library_management.model.User;
import com.example.library_management.repository.PostLikeRepository;
import com.example.library_management.repository.PostRepository;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Page<PostResponse> getPosts(int page, int limit, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Post> posts = postRepository.findAll(pageable);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        List<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUser(currentUser);

        return posts.map(post -> new PostResponse(post, likedPostIds.contains(post.getId())));
    }
}
