package com.example.library_management.service.genre;

import com.example.library_management.exception.ApiException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeleteGenreService {
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private MessageSource messageSource;

    public String deleteGenre(Long id){
        Genre genre = genreRepository.findById(id).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "GENRE-NOT-FOUND", messageSource.getMessage("error.genre.not.found", null, LocaleContextHolder.getLocale())));

        List<Book> booksWithGenre = bookRepository.findByGenresContaining(genre);
        booksWithGenre.forEach(book -> book.getGenres().remove(genre));
        bookRepository.saveAll(booksWithGenre);
        logger.log("Removed genre {} from all books", genre.getName());

        genreRepository.delete(genre);
        logger.log("Deleted genre {}, ID #{}", genre.getName(), genre.getId());
        return messageSource.getMessage("genre.delete", null, LocaleContextHolder.getLocale());
    }
}
