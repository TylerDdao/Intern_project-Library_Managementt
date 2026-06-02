package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.response.BookResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateBookService {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    GenreRepository genreRepository;

    public BookResponse updateBook(BookRequest request){
        Book book = bookRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if(request.getTitle() != null) book.setTitle(request.getTitle());
        if(request.getAuthor() != null) book.setTitle(request.getTitle());
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
        return new BookResponse(book);
    }
}
