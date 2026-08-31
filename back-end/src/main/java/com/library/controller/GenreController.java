package com.library.controller;

import com.library.dto.request.CreateGenreRequest;
import com.library.dto.request.UpdateGenreRequest;
import com.library.dto.response.ApiErrorResponse;
import com.library.dto.response.GenreResponse;
import com.library.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @Operation(
            summary = "Create a genre",
            description = "Creates a new genre."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Genre created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Genre already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(
            @Valid @RequestBody CreateGenreRequest request) {

        GenreResponse response = genreService.createGenre(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all genres",
            description = "Retrieves all genres. This endpoint is publicly accessible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genres retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<GenreResponse>> getGenres() {

        return ResponseEntity.ok(
                genreService.getAllGenres()
        );
    }

    @Operation(
            summary = "Get a genre by ID",
            description = "Retrieves a genre by its ID. This endpoint is publicly accessible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Genre not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenre(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                genreService.getGenreById(id)
        );
    }

    @Operation(
            summary = "Update a genre",
            description = "Updates an existing genre."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Genre not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Genre with the same name already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGenreRequest request) {

        return ResponseEntity.ok(
                genreService.updateGenre(id, request)
        );
    }

    @Operation(
            summary = "Delete a genre",
            description = "Deletes a genre."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Genre deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Genre not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Genre cannot be deleted because it is associated with one or more books",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(
            @PathVariable Long id) {

        genreService.deleteGenre(id);

        return ResponseEntity.noContent().build();
    }
}