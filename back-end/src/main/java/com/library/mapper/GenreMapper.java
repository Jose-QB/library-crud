package com.library.mapper;

import com.library.dto.request.CreateGenreRequest;
import com.library.dto.request.UpdateGenreRequest;
import com.library.dto.response.GenreResponse;
import com.library.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public Genre toEntity(CreateGenreRequest request) {
        if (request == null) {
            return null;
        }

        return Genre.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntity(
            Genre genre,
            UpdateGenreRequest request) {

        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
    }

    public GenreResponse toResponse(Genre genre) {
        if (genre == null) {
            return null;
        }

        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
}