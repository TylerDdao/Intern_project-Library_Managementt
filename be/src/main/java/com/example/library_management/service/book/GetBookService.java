package com.example.library_management.service.book;

import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.User;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class GetBookService {
    @Autowired
    BookRepository bookRepository;

    public Page<BookResponse> getMostBorrowedBooks(int page, int limit){
        Pageable pageable = PageRequest.of(page,limit);
        Page<Book> books;

        books = bookRepository.findMostBorrowedBooks(pageable);

        return books.map(BookResponse::new);
    }

    public Page<BookResponse> getMostPopularBooks(int page, int limit){
        Pageable pageable = PageRequest.of(page, limit);
        Page<Book> books;

        books = bookRepository.findMostPostsBooks(pageable);
        return books.map(BookResponse::new);
    }

    public Page<BookResponse> getRecentBooks(int page, int limit, String sortBy, String sortDir, int
            range){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Book> books;
        LocalDateTime since = LocalDateTime.now().minusDays(range);
        books = bookRepository.findRecentBooks(since, pageable);
        return books.map(BookResponse::new);
    }

    public Page<BookResponse> getBooks(int page, int limit, String sortBy, String sortDir, List<String> filterBy,
                                       String searchQuery) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Book> books;

        boolean hasFilter = filterBy != null && !filterBy.isEmpty();
        boolean hasQuery = searchQuery != null && !searchQuery.isBlank();

        if (hasFilter && hasQuery) {
            books = bookRepository.findBySearchQueryAndFilters(searchQuery, filterBy, pageable);
        } else if (hasFilter) {
            books = bookRepository.findByFilters(filterBy, pageable);
        } else if (hasQuery) {
            books = bookRepository.findBySearchQuery(searchQuery,pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }
        return books.map(BookResponse::new);
    }
}
