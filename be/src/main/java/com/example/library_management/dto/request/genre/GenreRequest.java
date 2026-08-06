package com.example.library_management.dto.request.genre;

import com.example.library_management.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreRequest {
    private Long id = null;
    private String name;
}
