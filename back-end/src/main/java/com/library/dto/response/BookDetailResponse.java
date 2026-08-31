package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetailResponse {

    private String id;

    private String title;

    private String isbn;

    private Short publicationYear;

    private String edition;

    private String language;

    private Short pages;

    private Integer stock;

    private String shelfLocation;

    private PublisherResponse publisher;

    private List<AuthorResponse> authors;

    private List<GenreResponse> genres;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}