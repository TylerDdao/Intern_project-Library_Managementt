package com.example.library_management.controller;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.model.Book;
import com.example.library_management.service.book.CreateBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    @Autowired
    CreateBookService createBookService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@RequestBody BookRequest request){
        return ResponseEntity.ok(ApiResponse.success(createBookService.addBook(request)));
    }
}
