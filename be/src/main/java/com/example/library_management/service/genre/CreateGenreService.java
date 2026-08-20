package com.example.library_management.service.genre;

import com.example.library_management.dto.request.genre.GenreRequest;
import com.example.library_management.dto.response.genre.GenreResponse;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.GenreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateGenreService {
    @Autowired
    private GenreRepository genreRepository;

    public GenreResponse createGenre(GenreRequest request){
        Genre genre = new Genre();

        genre.setName(request.getName());
        genreRepository.save(genre);
        return new GenreResponse(genre);
    }
}
