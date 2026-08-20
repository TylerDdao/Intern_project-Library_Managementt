package com.example.library_management.controller;

import com.example.library_management.dto.request.genre.GenreRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.genre.GenreResponse;
import com.example.library_management.service.genre.CreateGenreService;
import com.example.library_management.service.genre.DeleteGenreService;
import com.example.library_management.service.genre.GetGenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(summary = "Get genres", description = "Get all genres or get genre by name, require 'GET_GENRE' feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
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
    @Operation(summary = "Add a genre", description = "Add a new genre, require 'CREATE_GENRE' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public  ResponseEntity<ApiResponse<GenreResponse>> createGenre(
            @RequestBody GenreRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(createGenreService.createGenre(request)));
    }

    @PreAuthorize("@securityService.hasAccess('DELETE_GENRE')")
    @DeleteMapping()
    @Operation(summary = "Delete genre", description = "Delete a genre by ID, require 'DELETE_GENRE' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Genre not found",
                                            description = "Genre can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "GENRE-NOT-FOUND",
                                                    "message": "Genre not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<String>> deleteGenre(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(ApiResponse.success(deleteGenreService.deleteGenre(id)));
    }
}
