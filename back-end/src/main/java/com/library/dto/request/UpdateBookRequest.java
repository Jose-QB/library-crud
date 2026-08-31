package com.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 17, message = "ISBN must not exceed 17 characters")
    private String isbn;

    @Min(value = 1400, message = "Publication year must be at least 1400")
    @Max(value = 2100, message = "Publication year must not exceed 2100")
    private Short publicationYear;

    @Size(max = 30, message = "Edition must not exceed 30 characters")
    private String edition;

    @Size(max = 40, message = "Language must not exceed 40 characters")
    private String language;

    @Positive(message = "Pages must be greater than zero")
    private Short pages;

    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    @Size(max = 30, message = "Shelf location must not exceed 30 characters")
    private String shelfLocation;

    @NotNull(message = "Publisher ID is required")
    @Positive(message = "Publisher ID must be greater than zero")
    private Long publisherId;

    @NotEmpty(message = "At least one author is required")
    private List<
            @NotNull(message = "Author ID cannot be null")
            @Positive(message = "Author ID must be greater than zero")
                    Long
            > authorIds;

    @NotEmpty(message = "At least one genre is required")
    private List<
            @NotNull(message = "Genre ID cannot be null")
            @Positive(message = "Genre ID must be greater than zero")
                    Long
            > genreIds;
}