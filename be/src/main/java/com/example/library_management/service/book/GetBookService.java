package com.example.library_management.service.book;

import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.User;
import com.example.library_management.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GetBookService {
    @Autowired
    BookRepository bookRepository;

    public Page<BookResponse> getBooks(int page, int limit, String sortBy, String sortDir,
                                       String title, String author, String genre) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Book> books;

        if (title != null) {
            books = bookRepository.findByTitleContaining(title, pageable);
        } else if (author != null) {
            books = bookRepository.findByAuthorContaining(author, pageable);
        } else if (genre != null) {
            books = bookRepository.findByGenres_NameContaining(genre, pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }

        return books.map(BookResponse::new);
    }
}
