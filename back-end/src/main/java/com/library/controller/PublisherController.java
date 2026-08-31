package com.library.controller;

import com.library.dto.request.CreatePublisherRequest;
import com.library.dto.request.UpdatePublisherRequest;
import com.library.dto.response.ApiErrorResponse;
import com.library.dto.response.PublisherResponse;
import com.library.service.PublisherService;
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
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @Operation(
            summary = "Create a publisher",
            description = "Creates a new publisher."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Publisher created successfully"
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
                    description = "Publisher already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(
            @Valid @RequestBody CreatePublisherRequest request) {

        PublisherResponse response =
                publisherService.createPublisher(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all publishers",
            description = "Retrieves all publishers. This endpoint is publicly accessible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Publishers retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<PublisherResponse>> getPublishers() {

        return ResponseEntity.ok(
                publisherService.getAllPublishers()
        );
    }

    @Operation(
            summary = "Get a publisher by ID",
            description = "Retrieves a publisher by its ID. This endpoint is publicly accessible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Publisher retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Publisher not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getPublisher(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                publisherService.getPublisherById(id)
        );
    }

    @Operation(
            summary = "Update a publisher",
            description = "Updates an existing publisher."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Publisher updated successfully"
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
                    description = "Publisher not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Publisher with the same name already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PublisherResponse> updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePublisherRequest request) {

        return ResponseEntity.ok(
                publisherService.updatePublisher(id, request)
        );
    }

    @Operation(
            summary = "Delete a publisher",
            description = "Deletes a publisher."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Publisher deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Publisher not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Publisher cannot be deleted because it is associated with one or more books",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(
            @PathVariable Long id) {

        publisherService.deletePublisher(id);

        return ResponseEntity.noContent().build();
    }
}