package com.example.library_management.controller;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.service.book.CreateBookService;
import com.example.library_management.service.book.DeleteBookService;
import com.example.library_management.service.book.GetBookService;
import com.example.library_management.service.book.UpdateBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {
    @Autowired
    CreateBookService createBookService;

    @Autowired
    GetBookService getBookService;

    @Autowired
    UpdateBookService updateBookService;

    @Autowired
    DeleteBookService deleteBookService;

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getBooks(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) List<String> filterBy) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBooks(page, limit, sortBy, sortDir, filterBy, searchQuery))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/books/newly-arrived")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getRecentBooks(
            @RequestParam(defaultValue = "5") int dayRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getRecentBooks(page, limit, sortBy, sortDir, dayRange))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/books/most-posts")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getMostPostBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getMostPopularBooks(page, limit))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/books/most-borrowed")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getMostBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getMostBorrowedBooks(page, limit))
        );
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_BOOK')")
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@RequestBody BookRequest request){
        return ResponseEntity.ok(ApiResponse.success(createBookService.addBook(request)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_BOOK')")
    @PatchMapping("/book")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(@RequestBody BookRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateBookService.updateBook(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_BOOK')")
    @DeleteMapping("/book")
    public ResponseEntity<ApiResponse<String>> deleteBook(@RequestBody BookRequest request){
        return  ResponseEntity.ok(ApiResponse.success(deleteBookService.deleteBook(request)));
    }
}
