package com.example.library_management.service.comment;

import com.example.library_management.dto.response.comment.CommentResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Comment;
import com.example.library_management.repository.CommentRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetCommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private MessageSource messageSource;

    public Page<CommentResponse> getCommentByPostId(int page, int limit, String sortBy, String sortDir, Long postId){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        postRepository.findById(postId).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "POST-NOT-FOUND", messageSource.getMessage("error.post.not.found", null, LocaleContextHolder.getLocale())));
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

        return comments.map(comment -> new CommentResponse(comment, comment.getCreatedBy().equals(username)));
    }
}
