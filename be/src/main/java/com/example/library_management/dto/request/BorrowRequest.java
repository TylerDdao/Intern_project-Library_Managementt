package com.example.library_management.dto.request;

import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BorrowRequest {
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDateTime dueDate;
}
