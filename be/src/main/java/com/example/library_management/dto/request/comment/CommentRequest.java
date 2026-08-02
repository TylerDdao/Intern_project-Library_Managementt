package com.example.library_management.dto.request.comment;

import com.example.library_management.dto.response.book.BookResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentRequest {
    private Long id = null ;
    private Long postId = null ;
    private String content;
}
