package com.example.library_management.dto.response;

import com.example.library_management.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreResponse {
    private Long id;
    private String name;

    public GenreResponse(Genre genre){
        this.id = genre.getId();
        this.name = genre.getName();
    }
}
