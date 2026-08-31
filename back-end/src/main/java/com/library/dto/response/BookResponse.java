package com.library.dto.response;

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
public class BookResponse {

    private String id;

    private String title;

    private String isbn;

    private Short publicationYear;

    private String publisher;

    private List<String> authors;

    private List<String> genres;

    private Integer stock;

    private String shelfLocation;
}