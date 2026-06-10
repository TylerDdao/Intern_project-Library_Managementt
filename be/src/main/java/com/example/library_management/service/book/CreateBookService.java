package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import com.example.library_management.model.User;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.GenreRepository;
import com.example.library_management.util.AuditLogger;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreateBookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public BookResponse addBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        List<String> genresName = request.getGenres();
        List<Genre> genres = new ArrayList<>();
        genresName.forEach(name -> {
            Genre genre = genreRepository.findByName(name)
                    .orElseGet(() -> {
                        Genre newGenre = new Genre();
                        newGenre.setName(name);
                        return genreRepository.save(newGenre);
                    });
            genres.add(genre);
        });
        book.setGenres(genres);

        Book savedBook = bookRepository.save(book);
        logger.log("Created book ID#{} | Title: {} | Author: {}", savedBook.getId(), savedBook.getTitle(), savedBook.getAuthor());
        return new BookResponse(savedBook);
    }
}
