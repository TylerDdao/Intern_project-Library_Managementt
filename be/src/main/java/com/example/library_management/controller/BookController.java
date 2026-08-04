package com.example.library_management.controller;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.book.BookResponse;
import com.example.library_management.service.book.CreateBookService;
import com.example.library_management.service.book.DeleteBookService;
import com.example.library_management.service.book.GetBookService;
import com.example.library_management.service.book.UpdateBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
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
    @GetMapping("/unavailable")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getUnavailableBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getUnavailableBooks(page, limit, sortBy, sortDir))
        );
    }

    @GetMapping("/books-count/genre")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBooksCountByGenre() {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBooksCountByGenre())
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/book")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBook(id, title))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) List<String> filterBy,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBooks(page, limit, sortBy, sortDir, filterBy, searchQuery))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/newly-arrived")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getRecentBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getRecentBooks(page, limit, sortBy, sortDir))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/most-posts")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getMostPostBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getMostPopularBooks(page, limit))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/most-borrowed")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getMostBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getMostBorrowedBooks(page, limit))
        );
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_BOOK')")
    @PostMapping()
    public ResponseEntity<ApiResponse<BookResponse>> addBook(
            @RequestBody BookRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(createBookService.addBook(request)));
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_BOOK')")
    @PostMapping("/upload-book-cover")
    public ResponseEntity<ApiResponse<Boolean>> uploadBookCover(
            @RequestParam() Long id,
            @RequestPart(value = "file", required = false) MultipartFile file
    ){
        return ResponseEntity.ok(ApiResponse.success(createBookService.uploadBookCover(id, file)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_BOOK')")
    @PatchMapping("")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(@RequestBody BookRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateBookService.updateBook(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_BOOK')")
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<String>> deleteBook(@RequestBody BookRequest request){
        return  ResponseEntity.ok(ApiResponse.success(deleteBookService.deleteBook(request)));
    }
}
