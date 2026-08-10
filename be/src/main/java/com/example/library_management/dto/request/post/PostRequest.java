package com.example.library_management.dto.request.post;

import com.example.library_management.dto.response.book.BookResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
    private Long id = null ;
    private String subject;
    private String content;
    private Long likeCount = null;
    private Long commentCount = null;
    private Boolean isLiked = false;
    private Long book;
    private Boolean isEditable = false;
}
