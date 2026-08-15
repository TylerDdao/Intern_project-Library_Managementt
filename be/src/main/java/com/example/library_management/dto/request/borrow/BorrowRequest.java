package com.example.library_management.dto.request.borrow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BorrowRequest {
    private Long id = null;
    private Long bookId;
    private Long userId;
    private LocalDateTime dueDate;
    private Boolean isActive;
    private Float penalty = null;
}
