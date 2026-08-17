package com.example.library_management.service.genre;

import com.example.library_management.dto.request.genre.GenreRequest;
import com.example.library_management.dto.response.genre.GenreResponse;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.GenreRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateGenreService {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuditLogger logger;

    public GenreResponse createGenre(GenreRequest request){
        Genre genre = new Genre();

        genre.setName(request.getName());
        Genre saved = genreRepository.save(genre);
        logger.log("Created genre {} ID #{}", saved.getName(), saved.getId());
        return new GenreResponse(genre);
    }
}
