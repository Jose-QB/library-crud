package com.library.controller;

import com.library.dto.response.GenreResponse;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@Import(com.library.exception.GlobalExceptionHandler.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;


    // ============================================================
    // GET /api/genres
    // ============================================================

    @Test
    @DisplayName("GET /api/genres - should return all genres")
    void getGenres_shouldReturnAllGenres() throws Exception {

        List<GenreResponse> genres = List.of(
                GenreResponse.builder()
                        .id(1L)
                        .name("Fantasy")
                        .description("Fantasy literature")
                        .build(),

                GenreResponse.builder()
                        .id(2L)
                        .name("Science Fiction")
                        .description("Science fiction literature")
                        .build()
        );

        when(genreService.getAllGenres())
                .thenReturn(genres);

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Fantasy"))
                .andExpect(jsonPath("$[0].description")
                        .value("Fantasy literature"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name")
                        .value("Science Fiction"))
                .andExpect(jsonPath("$[1].description")
                        .value("Science fiction literature"));

        verify(genreService).getAllGenres();
    }


    // ============================================================
    // GET /api/genres/{id}
    // ============================================================

    @Test
    @DisplayName("GET /api/genres/{id} - should return genre")
    void getGenre_shouldReturnGenre() throws Exception {

        GenreResponse response = GenreResponse.builder()
                .id(1L)
                .name("Fantasy")
                .description("Fantasy literature")
                .build();

        when(genreService.getGenreById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Fantasy"))
                .andExpect(jsonPath("$.description")
                        .value("Fantasy literature"));

        verify(genreService).getGenreById(1L);
    }


    @Test
    @DisplayName("GET /api/genres/{id} - should return 404 when genre does not exist")
    void getGenre_shouldReturn404WhenGenreDoesNotExist()
            throws Exception {

        when(genreService.getGenreById(999L))
                .thenThrow(
                        new ResourceNotFoundException("Genre not found")
                );

        mockMvc.perform(get("/api/genres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Genre not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres/999"));

        verify(genreService).getGenreById(999L);
    }


    // ============================================================
    // POST /api/genres
    // ============================================================

    @Test
    @DisplayName("POST /api/genres - should create genre")
    void createGenre_shouldCreateGenre() throws Exception {

        GenreResponse response = GenreResponse.builder()
                .id(1L)
                .name("Fantasy")
                .description("Fantasy literature")
                .build();

        when(genreService.createGenre(any()))
                .thenReturn(response);

        String request = """
                {
                    "name": "Fantasy",
                    "description": "Fantasy literature"
                }
                """;

        mockMvc.perform(
                        post("/api/genres")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Fantasy"))
                .andExpect(jsonPath("$.description")
                        .value("Fantasy literature"));

        verify(genreService)
                .createGenre(any());
    }


    @Test
    @DisplayName("POST /api/genres - should return 400 when name is blank")
    void createGenre_shouldReturn400WhenNameIsBlank()
            throws Exception {

        String request = """
                {
                    "name": "",
                    "description": "Fantasy literature"
                }
                """;

        mockMvc.perform(
                        post("/api/genres")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres"))
                .andExpect(jsonPath("$.details[0]")
                        .value("name: Name is required"));
    }


    @Test
    @DisplayName("POST /api/genres - should return 400 when name exceeds 60 characters")
    void createGenre_shouldReturn400WhenNameExceedsMaxLength()
            throws Exception {

        String name = "A".repeat(61);

        String request = """
                {
                    "name": "%s",
                    "description": "Genre description"
                }
                """.formatted(name);

        mockMvc.perform(
                        post("/api/genres")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres"));
    }


    @Test
    @DisplayName("POST /api/genres - should return 409 when genre already exists")
    void createGenre_shouldReturn409WhenGenreAlreadyExists()
            throws Exception {

        when(genreService.createGenre(any()))
                .thenThrow(
                        new DuplicateResourceException(
                                "Genre already exists"
                        )
                );

        String request = """
                {
                    "name": "Fantasy",
                    "description": "Fantasy literature"
                }
                """;

        mockMvc.perform(
                        post("/api/genres")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Genre already exists"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres"));

        verify(genreService)
                .createGenre(any());
    }


    // ============================================================
    // PUT /api/genres/{id}
    // ============================================================

    @Test
    @DisplayName("PUT /api/genres/{id} - should update genre")
    void updateGenre_shouldUpdateGenre() throws Exception {

        GenreResponse response = GenreResponse.builder()
                .id(1L)
                .name("Dark Fantasy")
                .description("Fantasy with darker themes")
                .build();

        when(genreService.updateGenre(
                eq(1L),
                any()
        )).thenReturn(response);

        String request = """
                {
                    "name": "Dark Fantasy",
                    "description": "Fantasy with darker themes"
                }
                """;

        mockMvc.perform(
                        put("/api/genres/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Dark Fantasy"))
                .andExpect(jsonPath("$.description")
                        .value("Fantasy with darker themes"));

        verify(genreService)
                .updateGenre(eq(1L), any());
    }


    @Test
    @DisplayName("PUT /api/genres/{id} - should return 404 when genre does not exist")
    void updateGenre_shouldReturn404WhenGenreDoesNotExist()
            throws Exception {

        when(genreService.updateGenre(
                eq(999L),
                any()
        )).thenThrow(
                new ResourceNotFoundException("Genre not found")
        );

        String request = """
                {
                    "name": "Updated Genre",
                    "description": "Updated description"
                }
                """;

        mockMvc.perform(
                        put("/api/genres/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Genre not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres/999"));

        verify(genreService)
                .updateGenre(eq(999L), any());
    }


    @Test
    @DisplayName("PUT /api/genres/{id} - should return 400 when name is blank")
    void updateGenre_shouldReturn400WhenNameIsBlank()
            throws Exception {

        String request = """
                {
                    "name": "",
                    "description": "Updated description"
                }
                """;

        mockMvc.perform(
                        put("/api/genres/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres/1"))
                .andExpect(jsonPath("$.details[0]")
                        .value("name: Name is required"));
    }


    @Test
    @DisplayName("PUT /api/genres/{id} - should return 409 when genre name already exists")
    void updateGenre_shouldReturn409WhenNameAlreadyExists()
            throws Exception {

        when(genreService.updateGenre(
                eq(1L),
                any()
        )).thenThrow(
                new DuplicateResourceException(
                        "Genre with the same name already exists"
                )
        );

        String request = """
                {
                    "name": "Existing Genre",
                    "description": "Existing description"
                }
                """;

        mockMvc.perform(
                        put("/api/genres/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Genre with the same name already exists"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres/1"));

        verify(genreService)
                .updateGenre(eq(1L), any());
    }


    // ============================================================
    // DELETE /api/genres/{id}
    // ============================================================

    @Test
    @DisplayName("DELETE /api/genres/{id} - should delete genre")
    void deleteGenre_shouldDeleteGenre() throws Exception {

        doNothing()
                .when(genreService)
                .deleteGenre(1L);

        mockMvc.perform(delete("/api/genres/1"))
                .andExpect(status().isNoContent());

        verify(genreService).deleteGenre(1L);
    }


    @Test
    @DisplayName("DELETE /api/genres/{id} - should return 404 when genre does not exist")
    void deleteGenre_shouldReturn404WhenGenreDoesNotExist()
            throws Exception {

        doThrow(
                new ResourceNotFoundException("Genre not found")
        )
                .when(genreService)
                .deleteGenre(999L);

        mockMvc.perform(delete("/api/genres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Genre not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres/999"));

        verify(genreService).deleteGenre(999L);
    }


    @Test
    @DisplayName("DELETE /api/genres/{id} - should return 409 when genre is associated with books")
    void deleteGenre_shouldReturn409WhenGenreHasBooks()
            throws Exception {

        doThrow(
                new DuplicateResourceException(
                        "Genre cannot be deleted because it is associated with one or more books"
                )
        )
                .when(genreService)
                .deleteGenre(1L);

        mockMvc.perform(delete("/api/genres/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Genre cannot be deleted because it is associated with one or more books"
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/genres/1"));

        verify(genreService).deleteGenre(1L);
    }
}