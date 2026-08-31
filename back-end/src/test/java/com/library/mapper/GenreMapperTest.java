package com.library.mapper;

import com.library.dto.request.CreateGenreRequest;
import com.library.dto.request.UpdateGenreRequest;
import com.library.dto.response.GenreResponse;
import com.library.entity.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreMapperTest {

    private final GenreMapper mapper = new GenreMapper();

    @Test
    @DisplayName("toEntity - should map create request to entity")
    void toEntity_shouldMapRequestToEntity() {

        CreateGenreRequest request = CreateGenreRequest.builder()
                .name("Fantasy")
                .description("Fantasy literature")
                .build();

        Genre result = mapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Fantasy", result.getName());
        assertEquals("Fantasy literature", result.getDescription());
    }

    @Test
    @DisplayName("toEntity - should return null when request is null")
    void toEntity_shouldReturnNullWhenRequestIsNull() {

        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("updateEntity - should update entity with request values")
    void updateEntity_shouldUpdateEntity() {

        Genre genre = Genre.builder()
                .id(1L)
                .name("Old Genre")
                .description("Old description")
                .build();

        UpdateGenreRequest request = UpdateGenreRequest.builder()
                .name("Science Fiction")
                .description("Science fiction literature")
                .build();

        mapper.updateEntity(genre, request);

        assertEquals("Science Fiction", genre.getName());
        assertEquals(
                "Science fiction literature",
                genre.getDescription()
        );
        assertEquals(1L, genre.getId());
    }

    @Test
    @DisplayName("toResponse - should map entity to response")
    void toResponse_shouldMapEntityToResponse() {

        Genre genre = Genre.builder()
                .id(1L)
                .name("Fantasy")
                .description("Fantasy literature")
                .build();

        GenreResponse result = mapper.toResponse(genre);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Fantasy", result.getName());
        assertEquals("Fantasy literature", result.getDescription());
    }

    @Test
    @DisplayName("toResponse - should return null when entity is null")
    void toResponse_shouldReturnNullWhenEntityIsNull() {

        assertNull(mapper.toResponse(null));
    }
}