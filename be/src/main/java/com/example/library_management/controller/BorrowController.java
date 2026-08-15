package com.example.library_management.controller;

import com.example.library_management.dto.request.borrow.BorrowRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.borrow.BorrowResponse;
import com.example.library_management.service.borrow.CreateBorrowService;
import com.example.library_management.service.borrow.GetBorrowService;
import com.example.library_management.service.borrow.UpdateBorrowService;
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
    CreateBorrowService createBorrowService;
    @Autowired
    GetBorrowService getBorrowService;

    @Autowired
    UpdateBorrowService updateBorrowService;


    @PreAuthorize("@securityService.hasAccess('CREATE_BORROW')")
    @PostMapping()
    public ResponseEntity<ApiResponse<BorrowResponse>> createBorrow(@RequestBody BorrowRequest request){
        return ResponseEntity.ok(ApiResponse.success(createBorrowService.createBorrow(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_BORROW')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowResponse>> updateBorrow(
            @PathVariable Long id,
            @RequestBody BorrowRequest request
            ){
        request.setId(id);
        return ResponseEntity.ok(ApiResponse.success(updateBorrowService.updateBorrow(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_BORROW')")
    @PatchMapping("/return/{id}")
    public ResponseEntity<ApiResponse<BorrowResponse>> returnBorrow(
            @PathVariable Long id,
            @RequestBody BorrowRequest request
    ){
        request.setId(id);
        return ResponseEntity.ok(ApiResponse.success(updateBorrowService.returnBorrow(request)));
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW')")
    @GetMapping("/my-borrows")
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getMyBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "true") boolean isActive,
            @RequestParam(required = false) Long bookId
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getMyBorrows(page, limit, sortBy, sortDir, isActive, bookId))
        );
    }

    @GetMapping("/borrows-count/genre")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBorrowsCountsByGenre() {
        return ResponseEntity.ok(ApiResponse.success(getBorrowService.getBorrowCountsByGenre()));
    }

    @PreAuthorize("@securityService.hasAccess('GET_BORROW_MULTI')")
    @GetMapping("/{status}")
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getBorrowsByStatus(
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
    public ResponseEntity<ApiResponse<Page<BorrowResponse>>> getBorrowsBySearchQuery(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(
                ApiResponse.success(getBorrowService.getBorrows(page, limit, sortBy, sortDir, query))
        );
    }
}
