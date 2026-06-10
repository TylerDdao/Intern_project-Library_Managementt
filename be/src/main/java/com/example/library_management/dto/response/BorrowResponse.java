package com.example.library_management.dto.response;

import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BorrowResponse {
    private Long id;
    private BookResponse book;
    private UserResponse user;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private boolean isActive;

    public BorrowResponse(Borrow borrow) {
        this.id = borrow.getId();
        this.book = new BookResponse(borrow.getBook());
        this.user = new UserResponse(borrow.getUser());
        this.dueDate = borrow.getDueDate();
        this.createdAt = borrow.getCreatedAt();
        this.updatedAt = borrow.getUpdatedAt();
        this.createdBy = borrow.getCreatedBy();
        this.updatedBy = borrow.getUpdatedBy();
        this.isActive = borrow.getIsActive();
    }
}