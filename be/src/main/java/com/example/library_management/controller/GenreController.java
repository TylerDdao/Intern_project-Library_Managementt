package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.GenreResponse;
import com.example.library_management.model.Genre;
import com.example.library_management.service.genre.GetGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenreController {
    @Autowired
    private GetGenreService getGenreService;

    @PreAuthorize("@securityService.hasAccess('GET_GENRE')")
    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<Page<GenreResponse>>> getAllGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getGenreService.getGenres(page, limit, sortBy, sortDir))
        );
    }
}
