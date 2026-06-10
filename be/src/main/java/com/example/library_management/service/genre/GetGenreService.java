package com.example.library_management.service.genre;

import com.example.library_management.dto.response.GenreResponse;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.GenreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetGenreService {
    @Autowired
    private GenreRepository genreRepository;

    public Page<GenreResponse> getGenres(int page, int limit, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Genre> genres;

        genres = genreRepository.findAll(pageable);

        return genres.map(GenreResponse::new);
    }
}
