package com.library.mapper;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AuthorMapperTest {

    private AuthorMapper authorMapper;

    @BeforeEach
    void setUp() {
        authorMapper = new AuthorMapper();
    }

    // =========================================================
    // TO ENTITY
    // =========================================================

    @Test
    void toEntity_shouldMapCreateRequestToEntity() {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        Author result = authorMapper.toEntity(request);

        assertNotNull(result);
        assertEquals("Gabriel García Márquez", result.getName());
        assertEquals("Colombia", result.getCountry());
        assertEquals(
                LocalDate.of(1927, 3, 6),
                result.getBirthDate()
        );

        // The mapper does not assign the ID.
        assertNull(result.getId());
    }

    @Test
    void toEntity_shouldMapNullOptionalFields() {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("George Orwell")
                .build();

        Author result = authorMapper.toEntity(request);

        assertNotNull(result);
        assertEquals("George Orwell", result.getName());
        assertNull(result.getCountry());
        assertNull(result.getBirthDate());
        assertNull(result.getId());
    }

    @Test
    void toEntity_shouldReturnNullWhenRequestIsNull() {

        Author result = authorMapper.toEntity(null);

        assertNull(result);
    }

    // =========================================================
    // UPDATE ENTITY
    // =========================================================

    @Test
    void updateEntity_shouldUpdateAllFields() {

        Author author = Author.builder()
                .id(1L)
                .name("Old Name")
                .country("Old Country")
                .birthDate(LocalDate.of(1900, 1, 1))
                .build();

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("New Name")
                .country("New Country")
                .birthDate(LocalDate.of(1950, 5, 10))
                .build();

        authorMapper.updateEntity(author, request);

        assertEquals(1L, author.getId());

        assertEquals(
                "New Name",
                author.getName()
        );

        assertEquals(
                "New Country",
                author.getCountry()
        );

        assertEquals(
                LocalDate.of(1950, 5, 10),
                author.getBirthDate()
        );
    }

    @Test
    void updateEntity_shouldAllowNullOptionalFields() {

        Author author = Author.builder()
                .id(1L)
                .name("George Orwell")
                .country("United Kingdom")
                .birthDate(LocalDate.of(1903, 6, 25))
                .build();

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("George Orwell")
                .build();

        authorMapper.updateEntity(author, request);

        assertEquals(
                "George Orwell",
                author.getName()
        );

        assertNull(author.getCountry());
        assertNull(author.getBirthDate());

        // ID must not be modified by the mapper.
        assertEquals(1L, author.getId());
    }

    // =========================================================
    // TO RESPONSE
    // =========================================================

    @Test
    void toResponse_shouldMapEntityToResponse() {

        Author author = Author.builder()
                .id(1L)
                .name("J. R. R. Tolkien")
                .country("United Kingdom")
                .birthDate(LocalDate.of(1892, 1, 3))
                .build();

        AuthorResponse result =
                authorMapper.toResponse(author);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "J. R. R. Tolkien",
                result.getName()
        );
        assertEquals(
                "United Kingdom",
                result.getCountry()
        );
        assertEquals(
                LocalDate.of(1892, 1, 3),
                result.getBirthDate()
        );
    }

    @Test
    void toResponse_shouldMapNullOptionalFields() {

        Author author = Author.builder()
                .id(4L)
                .name("Gabriel García Márquez")
                .build();

        AuthorResponse result =
                authorMapper.toResponse(author);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals(
                "Gabriel García Márquez",
                result.getName()
        );
        assertNull(result.getCountry());
        assertNull(result.getBirthDate());
    }

    @Test
    void toResponse_shouldReturnNullWhenAuthorIsNull() {

        AuthorResponse result =
                authorMapper.toResponse(null);

        assertNull(result);
    }
}