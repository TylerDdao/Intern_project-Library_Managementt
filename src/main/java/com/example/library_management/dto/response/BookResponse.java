package com.example.library_management.dto.response;

import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BookResponse {
    private long id;
    private String title;
    private String author;
    private List<String> genres = new ArrayList<>();

    public BookResponse(Book book){
        this.id = book.getId();
        this.author = book.getAuthor();
        this.title = book.getTitle();
        List<Genre> genres = book.getGenres();
        genres.forEach(genre -> this.genres.add(genre.getName()));
    }
}
