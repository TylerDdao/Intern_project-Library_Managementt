package com.example.library_management.dto.response.book;

import com.example.library_management.dto.response.GenreResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String title;
    private String author;
    private List<GenreResponse> genres = new ArrayList<>();
    private int copies;
    private boolean isBorrowed;
    private String coverUrl;

    public BookResponse(Book book){
        this.id = book.getId();
        this.author = book.getAuthor();
        this.title = book.getTitle();
        List<Genre> genres = book.getGenres();
        genres.forEach(genre -> this.genres.add(new GenreResponse(genre)));
        this.copies = book.getCopies();
        this.isBorrowed = false;
        this.coverUrl = book.getCoverUrl();
    }
}
