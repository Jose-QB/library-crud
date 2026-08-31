package com.library.mapper;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AuthorMapperTest {

    private final AuthorMapper mapper = new AuthorMapper();

    @Test
    @DisplayName("toEntity - should map create request to entity")
    void toEntity_shouldMapRequestToEntity() {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        Author result = mapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Gabriel García Márquez", result.getName());
        assertEquals("Colombia", result.getCountry());
        assertEquals(
                LocalDate.of(1927, 3, 6),
                result.getBirthDate()
        );
    }

    @Test
    @DisplayName("toEntity - should return null when request is null")
    void toEntity_shouldReturnNullWhenRequestIsNull() {

        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("updateEntity - should update entity with request values")
    void updateEntity_shouldUpdateEntity() {

        Author author = Author.builder()
                .id(1L)
                .name("Old Name")
                .country("Mexico")
                .birthDate(LocalDate.of(1980, 1, 1))
                .build();

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        mapper.updateEntity(author, request);

        assertEquals("Gabriel García Márquez", author.getName());
        assertEquals("Colombia", author.getCountry());
        assertEquals(
                LocalDate.of(1927, 3, 6),
                author.getBirthDate()
        );
        assertEquals(1L, author.getId());
    }

    @Test
    @DisplayName("toResponse - should map entity to response")
    void toResponse_shouldMapEntityToResponse() {

        Author author = Author.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        AuthorResponse result = mapper.toResponse(author);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Gabriel García Márquez", result.getName());
        assertEquals("Colombia", result.getCountry());
        assertEquals(
                LocalDate.of(1927, 3, 6),
                result.getBirthDate()
        );
    }

    @Test
    @DisplayName("toResponse - should return null when entity is null")
    void toResponse_shouldReturnNullWhenEntityIsNull() {

        assertNull(mapper.toResponse(null));
    }
}