package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteBookService {
    @Autowired
    BookRepository bookRepository;

    public String deleteBook(BookRequest request){
        Book book = bookRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.delete(book);
        log.info("Deleting book {}, by {}", request.getTitle(), request.getAuthor());
        return "Book ID"+ book.getId() + " | Title: " + book.getTitle() + " | By: " + book.getAuthor() + " is deleted";
    }
}
