package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.dto.response.PostResponse;
import com.example.library_management.service.borrow.GetBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BorrowController {

    @Autowired
    GetBorrowService getBorrowService;

    @PreAuthorize("@securityService.hasAccess('GET_BORROW')")
    @GetMapping("/borrows")
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrows(page, limit, sortBy, sortDir, userId))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW')")
    @GetMapping("/borrow/nearest")
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getNearestBorrowByBookId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = true) Long bookId) {
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrows(page, limit, sortBy, sortDir, bookId))
        );
    }
}
