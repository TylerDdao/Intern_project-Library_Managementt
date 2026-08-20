package com.example.library_management.controller;

import com.example.library_management.dto.request.book.BookRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.book.BookResponse;
import com.example.library_management.service.book.CreateBookService;
import com.example.library_management.service.book.DeleteBookService;
import com.example.library_management.service.book.GetBookService;
import com.example.library_management.service.book.UpdateBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "Books", description = "Book management endpoints")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "BearerAuth")
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
    @Operation(summary = "Unavailable books", description = "Get all unavailable book, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/books-count/genre")
    @Operation(summary = "Book count by genre", description = "Get the number of book in each genre, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBooksCountByGenre() {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBooksCountByGenre())
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/book")
    @Operation(summary = "Get 1 book", description = "Get a specific book by ID or title, must have at least 1 parameter, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Book not found",
                                            description = "Book can't be found by its ID",
                                            value = """
                                                    {"code": "BOOK-NOT-FOUND",
                                                    "message": "Book not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBook(id, title))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping()
    @Operation(summary = "Get many books", description = "Get all books if no parameter is passed, or get books by query (title, author, genre, copies) and filter by book's criteria, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) List<String> filterBy,
            @RequestParam(required = false) String searchQuery) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getBooks(page, limit, sortBy, sortDir, filterBy, searchQuery))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/newly-arrived")
    @Operation(summary = "Get newly added books", description = "Get most recent added books, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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
    @Operation(summary = "Get most-post-about books", description = "Get books that has the highest number of post about it, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getMostPostBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getMostPopularBooks(page, limit))
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_BOOK')")
    @GetMapping("/most-borrowed")
    @Operation(summary = "Get newly added books", description = "Get books that has the highest number of borrow, require 'GET_BOOK' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getMostBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(getBookService.getMostBorrowedBooks(page, limit))
        );
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_BOOK')")
    @PostMapping()
    @Operation(summary = "Add book", description = "Add new book, require 'CREATE_BOOK' feature, default users shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<BookResponse>> addBook(
            @RequestBody BookRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(createBookService.addBook(request)));
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_BOOK')")
    @PostMapping("/upload-book-cover")
    @Operation(summary = "Upload book cover", description = "Upload a book cover by book ID, require 'CREATE_BOOK' feature, default users shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Book not found",
                                            description = "Book can't be found by its ID",
                                            value = """
                                                    {"code": "BOOK-NOT-FOUND",
                                                    "message": "Book not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Failed to upload book cover image",
                                            description = "The server can't upload the image file",
                                            value = """
                                                    {"code": "BOOK-COVER-UPLOAD-FAILED",
                                                    "message": "Failed to upload book cover",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<Boolean>> uploadBookCover(
            @RequestParam() Long id,
            @RequestPart(value = "file", required = false) MultipartFile file
    ){
        return ResponseEntity.ok(ApiResponse.success(createBookService.uploadBookCover(id, file)));
    }

    @PreAuthorize("@securityService.hasAccess('UPDATE_BOOK')")
    @PatchMapping("")
    @Operation(summary = "Update book", description = "Update a book by book ID, require 'UPDATE_BOOK' feature, default users shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Book not found",
                                            description = "Book can't be found by its ID",
                                            value = """
                                                    {"code": "BOOK-NOT-FOUND',
                                                    "message": "Book not found",
                                                    "data": null,
                                                    "timestamp": 2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(@RequestBody BookRequest request){
        return ResponseEntity.ok(ApiResponse.success(updateBookService.updateBook(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_BOOK')")
    @DeleteMapping("")
    @Operation(summary = "Delete book", description = "Delete a book by book ID, require 'DELETE_BOOK' feature, default users shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Book not found",
                                            description = "Book can't be found by its ID",
                                            value = """
                                                    {"code": "BOOK-NOT-FOUND',
                                                    "message": "Book not found",
                                                    "data": null,
                                                    "timestamp": 2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Active borrow for this book",
                                            description = "This book is still associated with active borrow(s)",
                                            value = """
                                                    {"code": "ACTIVE-BORROWS-EXIST',
                                                    "message": "There are active borrow(s) for this book",
                                                    "data": null,
                                                    "timestamp": 2026-08-19T10:00:00"}
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<String>> deleteBook(@RequestParam Long id){
        return  ResponseEntity.ok(ApiResponse.success(deleteBookService.deleteBook(id)));
    }
}
