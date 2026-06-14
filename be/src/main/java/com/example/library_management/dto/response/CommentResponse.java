package com.example.library_management.dto.response;

import com.example.library_management.model.Comment;
import com.example.library_management.model.Post;
import com.example.library_management.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommentResponse extends BaseResponse {
    private Long id;
    private String content;
    private PostResponse post;
    private boolean isEditable;

    public CommentResponse(Comment comment, boolean isEditable){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.post = new PostResponse(comment.getPost(), false, false);
        this.isEditable = isEditable;

        // 2. Set inherited parent fields directly
        this.setActive(comment.getIsActive());
        this.setDeleted(comment.getIsDeleted());
        this.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        this.setCreatedBy(comment.getCreatedBy());
        this.setUpdatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null);
        this.setUpdatedBy(comment.getUpdatedBy());
    }
}
