package com.example.library_management.service.comment;

import com.example.library_management.dto.request.comment.CommentRequest;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Comment;
import com.example.library_management.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DeleteCommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MessageSource messageSource;

    public String deleteComment(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "COMMENT-NOT-FOUND", messageSource.getMessage("error.comment.not.found", null, LocaleContextHolder.getLocale())));
        commentRepository.delete(comment);
        String message = messageSource.getMessage("comment.delete", null, LocaleContextHolder.getLocale());
        return message + " created by " + comment.getCreatedBy() + " at " + comment.getCreatedAt();
    }
}
