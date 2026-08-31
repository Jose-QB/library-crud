package com.library.service.impl;

import com.library.dto.request.CreateGenreRequest;
import com.library.dto.request.UpdateGenreRequest;
import com.library.dto.response.GenreResponse;
import com.library.entity.Genre;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.GenreMapper;
import com.library.repository.BookRepository;
import com.library.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;
    private GenreResponse genreResponse;

    @BeforeEach
    void setUp() {

        genre = Genre.builder()
                .id(1L)
                .name("Fantasy")
                .description("Fantasy literature")
                .build();

        genreResponse = GenreResponse.builder()
                .id(1L)
                .name("Fantasy")
                .description("Fantasy literature")
                .build();
    }

    @Test
    void shouldCreateGenre() {

        CreateGenreRequest request =
                CreateGenreRequest.builder()
                        .name("Fantasy")
                        .description("Fantasy literature")
                        .build();

        when(genreRepository.existsByNameIgnoreCase("Fantasy"))
                .thenReturn(false);

        when(genreMapper.toEntity(request))
                .thenReturn(genre);

        when(genreRepository.save(genre))
                .thenReturn(genre);

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        GenreResponse result =
                genreService.createGenre(request);

        assertThat(result).isEqualTo(genreResponse);

        verify(genreRepository)
                .existsByNameIgnoreCase("Fantasy");

        verify(genreMapper).toEntity(request);
        verify(genreRepository).save(genre);
        verify(genreMapper).toResponse(genre);
    }

    @Test
    void shouldTrimGenreNameWhenCreating() {

        CreateGenreRequest request =
                CreateGenreRequest.builder()
                        .name("  Fantasy  ")
                        .build();

        when(genreRepository.existsByNameIgnoreCase("Fantasy"))
                .thenReturn(false);

        when(genreMapper.toEntity(request))
                .thenReturn(genre);

        when(genreRepository.save(genre))
                .thenReturn(genre);

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        genreService.createGenre(request);

        assertThat(genre.getName())
                .isEqualTo("Fantasy");
    }

    @Test
    void shouldRejectDuplicateGenre() {

        CreateGenreRequest request =
                CreateGenreRequest.builder()
                        .name("Fantasy")
                        .build();

        when(genreRepository.existsByNameIgnoreCase("Fantasy"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                genreService.createGenre(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "A genre with name 'Fantasy' already exists"
                );

        verify(genreRepository, never()).save(any());
        verify(genreMapper, never()).toEntity(any());
    }

    @Test
    void shouldGetAllGenres() {

        Genre secondGenre = Genre.builder()
                .id(2L)
                .name("Dystopian")
                .build();

        GenreResponse secondResponse =
                GenreResponse.builder()
                        .id(2L)
                        .name("Dystopian")
                        .build();

        when(genreRepository.findAll())
                .thenReturn(List.of(genre, secondGenre));

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        when(genreMapper.toResponse(secondGenre))
                .thenReturn(secondResponse);

        List<GenreResponse> result =
                genreService.getAllGenres();

        assertThat(result)
                .containsExactly(
                        genreResponse,
                        secondResponse
                );

        verify(genreRepository).findAll();
    }

    @Test
    void shouldGetGenreById() {

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        GenreResponse result =
                genreService.getGenreById(1L);

        assertThat(result).isEqualTo(genreResponse);

        verify(genreRepository).findById(1L);
        verify(genreMapper).toResponse(genre);
    }

    @Test
    void shouldThrowExceptionWhenGenreDoesNotExist() {

        when(genreRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                genreService.getGenreById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Genre with ID 99 not found"
                );

        verifyNoInteractions(genreMapper);
    }

    @Test
    void shouldUpdateGenre() {

        UpdateGenreRequest request =
                UpdateGenreRequest.builder()
                        .name("Science Fiction")
                        .description("Science fiction")
                        .build();

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(genreRepository.existsByNameIgnoreCase(
                "Science Fiction"))
                .thenReturn(false);

        when(genreRepository.save(genre))
                .thenReturn(genre);

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        GenreResponse result =
                genreService.updateGenre(1L, request);

        assertThat(result).isEqualTo(genreResponse);

        assertThat(request.getName())
                .isEqualTo("Science Fiction");

        verify(genreMapper)
                .updateEntity(genre, request);

        verify(genreRepository).save(genre);
    }

    @Test
    void shouldTrimGenreNameWhenUpdating() {

        UpdateGenreRequest request =
                UpdateGenreRequest.builder()
                        .name("  Fantasy  ")
                        .build();

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(genreRepository.save(genre))
                .thenReturn(genre);

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        genreService.updateGenre(1L, request);

        assertThat(request.getName())
                .isEqualTo("Fantasy");
    }

    @Test
    void shouldRejectDuplicateGenreWhenUpdating() {

        UpdateGenreRequest request =
                UpdateGenreRequest.builder()
                        .name("Dystopian")
                        .build();

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(genreRepository.existsByNameIgnoreCase(
                "Dystopian"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                genreService.updateGenre(1L, request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(genreRepository, never()).save(any());
        verify(genreMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void shouldNotCheckDuplicateWhenOnlyCaseChanges() {

        genre.setName("Fantasy");

        UpdateGenreRequest request =
                UpdateGenreRequest.builder()
                        .name("fantasy")
                        .build();

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(genreRepository.save(genre))
                .thenReturn(genre);

        when(genreMapper.toResponse(genre))
                .thenReturn(genreResponse);

        genreService.updateGenre(1L, request);

        verify(genreRepository, never())
                .existsByNameIgnoreCase(anyString());

        verify(genreRepository).save(genre);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingGenre() {

        UpdateGenreRequest request =
                UpdateGenreRequest.builder()
                        .name("Fantasy")
                        .build();

        when(genreRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                genreService.updateGenre(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(genreRepository, never()).save(any());
    }

    @Test
    void shouldDeleteGenre() {

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(bookRepository.existsByGenreId(1L))
                .thenReturn(false);

        genreService.deleteGenre(1L);

        verify(genreRepository).findById(1L);
        verify(bookRepository).existsByGenreId(1L);
        verify(genreRepository).deleteById(1L);
    }

    @Test
    void shouldNotDeleteGenreAssociatedWithBooks() {

        when(genreRepository.findById(1L))
                .thenReturn(Optional.of(genre));

        when(bookRepository.existsByGenreId(1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                genreService.deleteGenre(1L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "Cannot delete genre with ID 1"
                );

        verify(genreRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingGenre() {

        when(genreRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                genreService.deleteGenre(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, never())
                .existsByGenreId(anyLong());

        verify(genreRepository, never())
                .deleteById(anyLong());
    }
}