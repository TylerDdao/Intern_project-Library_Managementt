package com.example.library_management.service.post;

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

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final MessageSource messageSource;
    private final AuditLogger logger;

    public String toggleLike(Long postId, Long userId) {
        if (postId == null || userId == null) {
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