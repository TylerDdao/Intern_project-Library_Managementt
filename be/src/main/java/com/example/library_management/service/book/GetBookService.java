package com.example.library_management.service.book;

import com.example.library_management.dto.response.book.BookResponse;
import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GetBookService {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    MessageSource messageSource;

    public Page<BookResponse> getUnavailableBooks(int page, int limit, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit);
        Page<Book> books;

        books = bookRepository.findByCopies(0, pageable);

        return books.map(BookResponse::new);
    }

    public Map<String, Long> getBooksCountByGenre(){
        List<Object[]> results = bookRepository.countBooksByGenre();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }

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
        books = bookRepository.findMostRecentBooks(pageable);
        return books.map(BookResponse::new);
    }

    public BookResponse getBook(Long bookId, String title){
        if(bookId != null){
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
            return new BookResponse(book);
        }
        else {
            Book book = bookRepository.findByTitle(title)
                    .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
            return new BookResponse(book);
        }

    }

    public Page<BookResponse> getBooks(int page, int limit, String sortBy, String sortDir, List<String> filterBy, String searchQuery) {
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
