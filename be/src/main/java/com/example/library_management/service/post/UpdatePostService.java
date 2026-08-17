package com.example.library_management.service.post;

import com.example.library_management.dto.request.post.PostRequest;
import com.example.library_management.dto.response.post.PostResponse;
import com.example.library_management.model.Post;
import com.example.library_management.model.PostLike;
import com.example.library_management.model.User;
import com.example.library_management.repository.PostLikeRepository;
import com.example.library_management.repository.PostRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdatePostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostLikeRepository postLikeRepository;
    private MessageSource messageSource;
    private AuditLogger logger;

    public PostResponse updatePost(PostRequest request){
        Post post = postRepository.findById(request.getId()).orElseThrow(()->new RuntimeException(messageSource.getMessage("error.post.not.found", null, LocaleContextHolder.getLocale())));
        if(request.getSubject() != null) post.setSubject(request.getSubject());
        if(request.getContent() != null) post.setContent(request.getContent());

        Post savedPost = postRepository.save(post);
        return new PostResponse(savedPost);
    }

    public String toggleLike(Long postId, Long userId) {
        if (postId == null || userId == null) {;
            return messageSource.getMessage("post.invalid.post.id.or.user.id", null, LocaleContextHolder.getLocale());
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("post.post.id.not.found", null, LocaleContextHolder.getLocale())));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("user.user.id.not.found", null, LocaleContextHolder.getLocale())));

        Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);

        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get()); // delete the actual fetched entity
            return messageSource.getMessage("post.remove.like", null, LocaleContextHolder.getLocale());
        } else {
            PostLike postLike = new PostLike();
            postLike.setPost(post);
            postLike.setUser(user);
            postLikeRepository.save(postLike);
            return messageSource.getMessage("post.add.like", null, LocaleContextHolder.getLocale());
        }
    }
}