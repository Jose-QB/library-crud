package com.library.service.impl;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

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

    @Test
    void shouldCreateAuthor() {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel García Márquez"))
                .thenReturn(false);

        when(authorMapper.toEntity(request))
                .thenReturn(author);

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        AuthorResponse result =
                authorService.createAuthor(request);

        assertThat(result).isEqualTo(authorResponse);
        assertThat(author.getName())
                .isEqualTo("Gabriel García Márquez");

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
    void shouldTrimAuthorNameWhenCreating() {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("  Gabriel García Márquez  ")
                .build();

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel García Márquez"))
                .thenReturn(false);

        when(authorMapper.toEntity(request))
                .thenReturn(author);

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        authorService.createAuthor(request);

        assertThat(author.getName())
                .isEqualTo("Gabriel García Márquez");
    }

    @Test
    void shouldRejectDuplicateAuthor() {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .build();

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel García Márquez"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                authorService.createAuthor(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "An author with name 'Gabriel García Márquez' already exists"
                );

        verify(authorRepository)
                .existsByNameIgnoreCase("Gabriel García Márquez");

        verify(authorRepository, never())
                .save(any());

        verify(authorMapper, never())
                .toEntity(any());
    }

    @Test
    void shouldGetAllAuthors() {

        Author secondAuthor = Author.builder()
                .id(2L)
                .name("George Orwell")
                .build();

        AuthorResponse secondResponse = AuthorResponse.builder()
                .id(2L)
                .name("George Orwell")
                .build();

        when(authorRepository.findAll())
                .thenReturn(List.of(author, secondAuthor));

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        when(authorMapper.toResponse(secondAuthor))
                .thenReturn(secondResponse);

        List<AuthorResponse> result =
                authorService.getAllAuthors();

        assertThat(result)
                .containsExactly(authorResponse, secondResponse);

        verify(authorRepository).findAll();
        verify(authorMapper).toResponse(author);
        verify(authorMapper).toResponse(secondAuthor);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoAuthors() {

        when(authorRepository.findAll())
                .thenReturn(List.of());

        List<AuthorResponse> result =
                authorService.getAllAuthors();

        assertThat(result).isEmpty();

        verify(authorRepository).findAll();
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldGetAuthorById() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        AuthorResponse result =
                authorService.getAuthorById(1L);

        assertThat(result).isEqualTo(authorResponse);

        verify(authorRepository).findById(1L);
        verify(authorMapper).toResponse(author);
    }

    @Test
    void shouldThrowExceptionWhenAuthorDoesNotExist() {

        when(authorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authorService.getAuthorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Author with ID 99 not found"
                );

        verify(authorRepository).findById(99L);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldUpdateAuthor() {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("Gabriel Garcia Marquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.existsByNameIgnoreCase(
                "Gabriel Garcia Marquez"))
                .thenReturn(false);

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        AuthorResponse result =
                authorService.updateAuthor(1L, request);

        assertThat(result).isEqualTo(authorResponse);

        verify(authorRepository).findById(1L);

        verify(authorRepository)
                .existsByNameIgnoreCase(
                        "Gabriel Garcia Marquez");

        verify(authorMapper)
                .updateEntity(author, request);

        verify(authorRepository).save(author);

        verify(authorMapper).toResponse(author);
    }

    @Test
    void shouldTrimAuthorNameWhenUpdating() {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("  George Orwell  ")
                .build();

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.existsByNameIgnoreCase(
                "George Orwell"))
                .thenReturn(false);

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        authorService.updateAuthor(1L, request);

        assertThat(author.getName())
                .isEqualTo("George Orwell");
    }

    @Test
    void shouldNotCheckDuplicateWhenOnlyCaseChanges() {

        author.setName("George Orwell");

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("george orwell")
                .build();

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.save(author))
                .thenReturn(author);

        when(authorMapper.toResponse(author))
                .thenReturn(authorResponse);

        authorService.updateAuthor(1L, request);

        verify(authorRepository, never())
                .existsByNameIgnoreCase(anyString());

        verify(authorRepository).save(author);
    }

    @Test
    void shouldRejectDuplicateNameWhenUpdating() {

        author.setName("Gabriel García Márquez");

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("George Orwell")
                .build();

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(authorRepository.existsByNameIgnoreCase(
                "George Orwell"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                authorService.updateAuthor(1L, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "An author with name 'George Orwell' already exists"
                );

        verify(authorRepository, never())
                .save(any());

        verify(authorMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAuthor() {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("George Orwell")
                .build();

        when(authorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authorService.updateAuthor(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Author with ID 99 not found"
                );

        verify(authorRepository, never())
                .save(any());
    }

    @Test
    void shouldDeleteAuthor() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(bookRepository.existsByAuthorId(1L))
                .thenReturn(false);

        authorService.deleteAuthor(1L);

        verify(authorRepository).findById(1L);
        verify(bookRepository).existsByAuthorId(1L);
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void shouldNotDeleteAuthorAssociatedWithBooks() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(bookRepository.existsByAuthorId(1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                authorService.deleteAuthor(1L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "Cannot delete author with ID 1"
                );

        verify(bookRepository)
                .existsByAuthorId(1L);

        verify(authorRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingAuthor() {

        when(authorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authorService.deleteAuthor(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Author with ID 99 not found"
                );

        verify(bookRepository, never())
                .existsByAuthorId(anyLong());

        verify(authorRepository, never())
                .deleteById(anyLong());
    }
}