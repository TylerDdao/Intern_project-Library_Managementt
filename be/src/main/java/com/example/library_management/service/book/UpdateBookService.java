package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.response.book.BookResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.GenreRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UpdateBookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    AuditLogger logger;

    @CacheEvict(value = "books", key = "#request.id")
    public BookResponse updateBook(BookRequest request){
        Book book = bookRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
        if(request.getTitle() != null) book.setTitle(request.getTitle());
        if(request.getAuthor() != null) book.setTitle(request.getAuthor());
        if(request.getGenres() != null){
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
        }
        Book savedBook = bookRepository.save(book);
        logger.log("Updated book ID #{} | Title: {} | Author: {}", savedBook.getId(), savedBook.getTitle(), savedBook.getAuthor());
        return new BookResponse(book);
    }
}
