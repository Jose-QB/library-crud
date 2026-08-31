package com.library.service;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorResponse authorResponse;

    @BeforeEach
    void setUp() {

        author = Author.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        authorResponse = AuthorResponse.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void createAuthor_shouldCreateAuthorSuccessfully() {

        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setName("Gabriel García Márquez");
        request.setCountry("Colombia");
        request.setBirthDate(LocalDate.of(1927, 3, 6));

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel García Márquez"
        )).thenReturn(false);

        when(authorMapper.toEntity(request))
                .thenReturn(author);

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        AuthorResponse result =
                authorService.createAuthor(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Gabriel García Márquez",
                result.getName()
        );

        verify(authorRepository)
                .existsByNameIgnoreCase("Gabriel García Márquez");

        verify(authorMapper)
                .toEntity(request);

        verify(authorRepository)
                .save(author);

        verify(authorMapper)
                .toResponse(author);
    }

    @Test
    void createAuthor_shouldTrimName() {

        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setName("  Gabriel García Márquez  ");
        request.setCountry("Colombia");

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel García Márquez"
        )).thenReturn(false);

        when(authorMapper.toEntity(request))
                .thenReturn(author);

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        authorService.createAuthor(request);

        verify(authorRepository)
                .existsByNameIgnoreCase(
                        "Gabriel García Márquez"
                );

        assertEquals(
                "Gabriel García Márquez",
                author.getName()
        );
    }

    @Test
    void createAuthor_shouldThrowExceptionWhenNameAlreadyExists() {

        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setName("Gabriel García Márquez");

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel García Márquez"
        )).thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> authorService.createAuthor(request)
                );

        assertEquals(
                "An author with name 'Gabriel García Márquez' already exists",
                exception.getMessage()
        );

        verify(authorRepository)
                .existsByNameIgnoreCase(
                        "Gabriel García Márquez"
                );

        verify(authorMapper, never()).toEntity(any());

        verify(authorRepository, never()).save(any());
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAllAuthors_shouldReturnAllAuthors() {

        Author secondAuthor = Author.builder()
                .id(2L)
                .name("Jorge Luis Borges")
                .country("Argentina")
                .build();

        AuthorResponse secondResponse = AuthorResponse.builder()
                .id(2L)
                .name("Jorge Luis Borges")
                .country("Argentina")
                .build();

        when(authorRepository.findAll())
                .thenReturn(List.of(author, secondAuthor));

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        when(authorMapper.toResponse(secondAuthor))
                .thenReturn(secondResponse);

        List<AuthorResponse> result =
                authorService.getAllAuthors();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "Gabriel García Márquez",
                result.get(0).getName()
        );

        assertEquals(
                "Jorge Luis Borges",
                result.get(1).getName()
        );

        verify(authorRepository).findAll();
        verify(authorMapper).toResponse(author);
        verify(authorMapper).toResponse(secondAuthor);
    }

    @Test
    void getAllAuthors_shouldReturnEmptyListWhenNoAuthorsExist() {

        when(authorRepository.findAll())
                .thenReturn(List.of());

        List<AuthorResponse> result =
                authorService.getAllAuthors();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(authorRepository).findAll();

        verifyNoInteractions(authorMapper);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getAuthorById_shouldReturnAuthor() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        AuthorResponse result =
                authorService.getAuthorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Gabriel García Márquez",
                result.getName()
        );

        verify(authorRepository)
                .findById(1L);

        verify(authorMapper)
                .toResponse(author);
    }

    @Test
    void getAuthorById_shouldThrowExceptionWhenAuthorDoesNotExist() {

        when(authorRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authorService.getAuthorById(999L)
                );

        assertEquals(
                "Author with ID 999 not found",
                exception.getMessage()
        );

        verify(authorRepository)
                .findById(999L);

        verifyNoInteractions(authorMapper);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void updateAuthor_shouldUpdateAuthorSuccessfully() {

        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("Gabriel García Márquez");
        request.setCountry("Colombia");
        request.setBirthDate(LocalDate.of(1927, 3, 6));

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        AuthorResponse result =
                authorService.updateAuthor(1L, request);

        assertNotNull(result);
        assertEquals(
                "Gabriel García Márquez",
                result.getName()
        );

        verify(authorRepository)
                .findById(1L);

        verify(authorMapper)
                .updateEntity(author, request);

        verify(authorRepository)
                .save(author);

        verify(authorMapper)
                .toResponse(author);

        verify(authorRepository, never())
                .existsByNameIgnoreCase(anyString());
    }

    @Test
    void updateAuthor_shouldTrimName() {

        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("  Gabriel García Márquez  ");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        authorService.updateAuthor(1L, request);

        assertEquals(
                "Gabriel García Márquez",
                author.getName()
        );

        verify(authorMapper)
                .updateEntity(author, request);

        verify(authorRepository)
                .save(author);
    }

    @Test
    void updateAuthor_shouldAllowKeepingSameName() {

        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("gabriel garcía márquez");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        authorService.updateAuthor(1L, request);

        verify(authorRepository, never())
                .existsByNameIgnoreCase(anyString());

        verify(authorMapper)
                .updateEntity(author, request);

        verify(authorRepository)
                .save(author);
    }

    @Test
    void updateAuthor_shouldThrowExceptionWhenNewNameAlreadyExists() {

        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("Jorge Luis Borges");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.existsByNameIgnoreCase(
                "Jorge Luis Borges"
        )).thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> authorService.updateAuthor(1L, request)
                );

        assertEquals(
                "An author with name 'Jorge Luis Borges' already exists",
                exception.getMessage()
        );

        verify(authorRepository)
                .findById(1L);

        verify(authorRepository)
                .existsByNameIgnoreCase("Jorge Luis Borges");

        verify(authorMapper, never())
                .updateEntity(any(), any());

        verify(authorRepository, never())
                .save(any());
    }

    @Test
    void updateAuthor_shouldThrowExceptionWhenAuthorDoesNotExist() {

        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("New Author");

        when(authorRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authorService.updateAuthor(999L, request)
                );

        assertEquals(
                "Author with ID 999 not found",
                exception.getMessage()
        );

        verify(authorRepository)
                .findById(999L);

        verifyNoInteractions(authorMapper);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void deleteAuthor_shouldDeleteAuthorSuccessfully() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(bookRepository.existsByAuthorId(1L))
                .thenReturn(false);

        authorService.deleteAuthor(1L);

        verify(authorRepository)
                .findById(1L);

        verify(bookRepository)
                .existsByAuthorId(1L);

        verify(authorRepository)
                .deleteById(1L);
    }

    @Test
    void deleteAuthor_shouldThrowExceptionWhenAuthorDoesNotExist() {

        when(authorRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authorService.deleteAuthor(999L)
                );

        assertEquals(
                "Author with ID 999 not found",
                exception.getMessage()
        );

        verify(authorRepository)
                .findById(999L);

        verifyNoInteractions(bookRepository);

        verify(authorRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void deleteAuthor_shouldThrowExceptionWhenAuthorHasBooks() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(bookRepository.existsByAuthorId(1L))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> authorService.deleteAuthor(1L)
                );

        assertEquals(
                "Cannot delete author with ID 1 because it is associated with one or more books",
                exception.getMessage()
        );

        verify(authorRepository)
                .findById(1L);

        verify(bookRepository)
                .existsByAuthorId(1L);

        verify(authorRepository, never())
                .deleteById(anyLong());
    }
}