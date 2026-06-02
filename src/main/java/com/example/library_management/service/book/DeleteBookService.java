package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteBookService {
    @Autowired
    BookRepository bookRepository;

    public String deleteBook(BookRequest request){
        Book book = bookRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.delete(book);
        return "Book ID"+ book.getId() + " | Title: " + book.getTitle() + " | By: " + book.getAuthor() + " is deleted";
    }
}
