package com.example.library_management.service.post;

import com.example.library_management.model.Post;
import com.example.library_management.repository.CommentRepository;
import com.example.library_management.repository.PostLikeRepository;
import com.example.library_management.repository.PostRepository;
import com.example.library_management.util.AuditLogger;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DeletePostService {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Transactional
    public String deletePost(Long id){
        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException(messageSource.getMessage("error.post.not.found", null, LocaleContextHolder.getLocale())));
        commentRepository.deleteAllByPost(post);
        postLikeRepository.deleteAllByPost(post);
        postRepository.delete(post);
        logger.log("Deleted post ID#{} and all of its comments", post.getId());
        String message = messageSource.getMessage("post.delete", null, LocaleContextHolder.getLocale());
        return message;
    }
}
