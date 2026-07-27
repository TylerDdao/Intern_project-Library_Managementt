package com.example.library_management.dto.request.genre;

import com.example.library_management.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreRequest {
    private Long id;
    private String name;

    public GenreRequest(Genre genre){
        this.id = genre.getId();
        this.name = genre.getName();
    }
}
