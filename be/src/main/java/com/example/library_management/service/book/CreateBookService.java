package com.example.library_management.service.book;

import com.example.library_management.dto.request.book.BookRequest;
import com.example.library_management.dto.response.book.BookResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.GenreRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${app.upload.dir}")
    private String uploadDir;

    public boolean uploadBookCover(Long id, MultipartFile file) {
        try {
            Book book = bookRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "BOOK-NOT-FOUND", messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
            String fileName =  book.getId() + ".webp";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            Files.write(uploadPath.resolve(fileName), file.getBytes());
            book.setCoverUrl(fileName);
            bookRepository.save(book);
            return true;
        } catch (IOException e) {
            log.error("Failed to save book cover: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "BOOK-COVER-UPLOAD-FAILED", messageSource.getMessage("error.book.cover.upload.failed", null, LocaleContextHolder.getLocale())
            );
        }
    }

    public BookResponse addBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCopies(request.getCopies());

        // handle genres
        List<Genre> genres = new ArrayList<>();
        request.getGenres().forEach(name -> {
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
