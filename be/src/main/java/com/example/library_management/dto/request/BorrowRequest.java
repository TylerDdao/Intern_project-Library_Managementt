package com.example.library_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BorrowRequest {
    private Long id = null;
    private Long bookId;
    private Long userId;
    private LocalDateTime dueDate;
    private Boolean isActive;
}
