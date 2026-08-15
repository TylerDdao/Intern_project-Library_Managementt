package com.example.library_management.controller;

import com.example.library_management.dto.request.genre.GenreRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.genre.GenreResponse;
import com.example.library_management.service.genre.CreateGenreService;
import com.example.library_management.service.genre.DeleteGenreService;
import com.example.library_management.service.genre.GetGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
public class GenreController {
    @Autowired
    private GetGenreService getGenreService;
    @Autowired
    private CreateGenreService createGenreService;
    @Autowired
    private DeleteGenreService deleteGenreService;

    @PreAuthorize("@securityService.hasAccess('GET_GENRE')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<GenreResponse>>> getAllGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name
    ){
        return ResponseEntity.ok(
                ApiResponse.success(getGenreService.getGenres(page, limit, sortBy, sortDir, name))
        );
    }

    @PreAuthorize("@securityService.hasAccess('CREATE_GENRE')")
    @PostMapping()
    public  ResponseEntity<ApiResponse<GenreResponse>> createGenre(
            @RequestBody GenreRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(createGenreService.createGenre(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_GENRE')")
    @DeleteMapping()
    public ResponseEntity<ApiResponse<String>> deleteGenre(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(ApiResponse.success(deleteGenreService.deleteGenre(id)));
    }
}
