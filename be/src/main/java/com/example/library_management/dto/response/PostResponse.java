package com.example.library_management.dto.response;

import com.example.library_management.dto.response.book.BookResponse;
import com.example.library_management.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostResponse {
    private long id;
    private String subject;
    private String content;
    private long likeCount;
    private long commentCount;
    private boolean isLiked;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private BookResponse book;
    private boolean isEditable;

    public PostResponse(Post post, boolean isLiked, boolean isEditable) {
        this.id = post.getId();
        this.subject = post.getSubject();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.isLiked = isLiked;
        this.createdBy = post.getCreatedBy();
        this.createdAt = post.getCreatedAt();
        this.updatedBy = post.getUpdatedBy();
        this.updatedAt = post.getUpdatedAt();
        this.book = new BookResponse(post.getBook());
        this.isEditable = isEditable;
    }
}