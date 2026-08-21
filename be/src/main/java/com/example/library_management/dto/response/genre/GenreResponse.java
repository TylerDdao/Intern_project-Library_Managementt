package com.example.library_management.dto.response.genre;

import com.example.library_management.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class GenreResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;

    public GenreResponse(Genre genre){
        this.id = genre.getId();
        this.name = genre.getName();
    }
}
