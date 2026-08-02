package com.example.library_management.service.comment;

import com.example.library_management.dto.request.comment.CommentRequest;
import com.example.library_management.model.Comment;
import com.example.library_management.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DeleteCommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MessageSource messageSource;

    public boolean deleteComment(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new RuntimeException((messageSource.getMessage("error.comment.not.found", null, LocaleContextHolder.getLocale()))));
        commentRepository.delete(comment);
        return true;
    }
}
