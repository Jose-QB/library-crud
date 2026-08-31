package com.library.controller;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.ApiErrorResponse;
import com.library.dto.response.AuthorResponse;
import com.library.service.AuthorService;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @Operation(
            summary = "Create an author",
            description = "Creates a new author."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Author created successfully"
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
                    description = "Author already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(
            @Valid @RequestBody CreateAuthorRequest request) {

        AuthorResponse response =
                authorService.createAuthor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all authors",
            description = "Retrieves all authors. This endpoint is publicly accessible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authors retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAuthors() {

        return ResponseEntity.ok(
                authorService.getAllAuthors()
        );
    }

    @Operation(
            summary = "Get an author by ID",
            description = "Retrieves an author by its ID. This endpoint is publicly accessible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthor(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                authorService.getAuthorById(id)
        );
    }

    @Operation(
            summary = "Update an author",
            description = "Updates an existing author."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author updated successfully"
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
                    description = "Author not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Author with the same name already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAuthorRequest request) {

        return ResponseEntity.ok(
                authorService.updateAuthor(id, request)
        );
    }

    @Operation(
            summary = "Delete an author",
            description = "Deletes an author."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Author deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Author cannot be deleted because it is associated with one or more books",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable Long id) {

        authorService.deleteAuthor(id);

        return ResponseEntity.noContent().build();
    }
}