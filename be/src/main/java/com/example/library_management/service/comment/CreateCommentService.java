package com.example.library_management.service.comment;

import com.example.library_management.dto.request.comment.CommentRequest;
import com.example.library_management.dto.response.comment.CommentResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Comment;
import com.example.library_management.model.Post;
import com.example.library_management.repository.CommentRepository;
import com.example.library_management.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CreateCommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MessageSource messageSource;
    public CommentResponse createComment(CommentRequest request){
        Post post = postRepository.findById(request.getPostId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "POST-NOT-FOUND", messageSource.getMessage("error.post.not.found", null, LocaleContextHolder.getLocale())));
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new CommentResponse(savedComment, savedComment.getCreatedBy().equals(username));
    }
}
