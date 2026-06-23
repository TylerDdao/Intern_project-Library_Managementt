package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.service.borrow.GetBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/borrows")
public class BorrowController {

    @Autowired
    GetBorrowService getBorrowService;

    @GetMapping("/borrows-count/genre")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBorrowCountsByGenre() {
        return ResponseEntity.ok(ApiResponse.success(getBorrowService.getBorrowCountsByGenre()));
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW_MULTI')")
    @GetMapping("/{status}")
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getLateBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @PathVariable String status
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrowsByStatus(page, limit, sortBy, sortDir, status))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW_MULTI')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String searchQuery) {
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrows(page, limit, sortBy, sortDir, userId, searchQuery))
        );
    }
}
