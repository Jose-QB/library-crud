package com.library.controller;

import tools.jackson.databind.json.JsonMapper;
import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.AuthorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

@WebMvcTest(AuthorController.class)
@Import(com.library.exception.GlobalExceptionHandler.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;


    // ============================================================
    // GET /api/authors
    // ============================================================

    @Test
    @DisplayName("GET /api/authors - should return all authors")
    void getAuthors_shouldReturnAllAuthors() throws Exception {

        List<AuthorResponse> authors = List.of(
                AuthorResponse.builder()
                        .id(1L)
                        .name("Gabriel García Márquez")
                        .country("Colombia")
                        .birthDate(LocalDate.of(1927, 3, 6))
                        .build(),

                AuthorResponse.builder()
                        .id(2L)
                        .name("Jorge Luis Borges")
                        .country("Argentina")
                        .birthDate(LocalDate.of(1899, 8, 24))
                        .build()
        );

        when(authorService.getAllAuthors())
                .thenReturn(authors);

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Gabriel García Márquez"))
                .andExpect(jsonPath("$[0].country")
                        .value("Colombia"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name")
                        .value("Jorge Luis Borges"));

        verify(authorService).getAllAuthors();
    }


    // ============================================================
    // GET /api/authors/{id}
    // ============================================================

    @Test
    @DisplayName("GET /api/authors/{id} - should return author")
    void getAuthor_shouldReturnAuthor() throws Exception {

        AuthorResponse response = AuthorResponse.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        when(authorService.getAuthorById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Gabriel García Márquez"))
                .andExpect(jsonPath("$.country")
                        .value("Colombia"))
                .andExpect(jsonPath("$.birthDate")
                        .value("1927-03-06"));

        verify(authorService).getAuthorById(1L);
    }


    @Test
    @DisplayName("GET /api/authors/{id} - should return 404 when author does not exist")
    void getAuthor_shouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        when(authorService.getAuthorById(999L))
                .thenThrow(
                        new ResourceNotFoundException("Author not found")
                );

        mockMvc.perform(get("/api/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Author not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors/999"));

        verify(authorService).getAuthorById(999L);
    }


    // ============================================================
    // POST /api/authors
    // ============================================================

    @Test
    @DisplayName("POST /api/authors - should create author")
    void createAuthor_shouldCreateAuthor() throws Exception {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        AuthorResponse response = AuthorResponse.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        when(authorService.createAuthor(any(CreateAuthorRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Gabriel García Márquez"))
                .andExpect(jsonPath("$.country")
                        .value("Colombia"))
                .andExpect(jsonPath("$.birthDate")
                        .value("1927-03-06"));

        verify(authorService).createAuthor(any(CreateAuthorRequest.class));
    }


    @Test
    @DisplayName("POST /api/authors - should return 400 when name is blank")
    void createAuthor_shouldReturn400WhenNameIsBlank()
            throws Exception {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("")
                .country("Mexico")
                .birthDate(LocalDate.of(1980, 1, 1))
                .build();

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors"))
                .andExpect(jsonPath("$.details[0]")
                        .value("name: Name is required"));
    }


    @Test
    @DisplayName("POST /api/authors - should return 400 when name exceeds 120 characters")
    void createAuthor_shouldReturn400WhenNameExceedsMaxLength()
            throws Exception {

        String name = "A".repeat(121);

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name(name)
                .country("Mexico")
                .build();

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("POST /api/authors - should return 409 when author already exists")
    void createAuthor_shouldReturn409WhenAuthorAlreadyExists()
            throws Exception {

        CreateAuthorRequest request = CreateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .build();

        when(authorService.createAuthor(any(CreateAuthorRequest.class)))
                .thenThrow(
                        new DuplicateResourceException(
                                "Author already exists"
                        )
                );

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Author already exists"))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors"));

        verify(authorService)
                .createAuthor(any(CreateAuthorRequest.class));
    }


    // ============================================================
    // PUT /api/authors/{id}
    // ============================================================

    @Test
    @DisplayName("PUT /api/authors/{id} - should update author")
    void updateAuthor_shouldUpdateAuthor() throws Exception {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        AuthorResponse response = AuthorResponse.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .country("Colombia")
                .birthDate(LocalDate.of(1927, 3, 6))
                .build();

        when(authorService.updateAuthor(
                eq(1L),
                any(UpdateAuthorRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Gabriel García Márquez"))
                .andExpect(jsonPath("$.country")
                        .value("Colombia"));

        verify(authorService)
                .updateAuthor(eq(1L), any(UpdateAuthorRequest.class));
    }


    @Test
    @DisplayName("PUT /api/authors/{id} - should return 404 when author does not exist")
    void updateAuthor_shouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("Updated Author")
                .country("Mexico")
                .build();

        when(authorService.updateAuthor(
                eq(999L),
                any(UpdateAuthorRequest.class)
        )).thenThrow(
                new ResourceNotFoundException("Author not found")
        );

        mockMvc.perform(
                        put("/api/authors/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Author not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors/999"));
    }


    @Test
    @DisplayName("PUT /api/authors/{id} - should return 400 when name is blank")
    void updateAuthor_shouldReturn400WhenNameIsBlank()
            throws Exception {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("")
                .country("Mexico")
                .build();

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.details[0]")
                        .value("name: Name is required"));
    }


    @Test
    @DisplayName("PUT /api/authors/{id} - should return 409 when author name already exists")
    void updateAuthor_shouldReturn409WhenNameAlreadyExists()
            throws Exception {

        UpdateAuthorRequest request = UpdateAuthorRequest.builder()
                .name("Existing Author")
                .country("Mexico")
                .build();

        when(authorService.updateAuthor(
                eq(1L),
                any(UpdateAuthorRequest.class)
        )).thenThrow(
                new DuplicateResourceException(
                        "Author with the same name already exists"
                )
        );

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Author with the same name already exists"))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors/1"));
    }


    // ============================================================
    // DELETE /api/authors/{id}
    // ============================================================

    @Test
    @DisplayName("DELETE /api/authors/{id} - should delete author")
    void deleteAuthor_shouldDeleteAuthor() throws Exception {

        doNothing()
                .when(authorService)
                .deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());

        verify(authorService).deleteAuthor(1L);
    }


    @Test
    @DisplayName("DELETE /api/authors/{id} - should return 404 when author does not exist")
    void deleteAuthor_shouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        doThrow(
                new ResourceNotFoundException("Author not found")
        )
                .when(authorService)
                .deleteAuthor(999L);

        mockMvc.perform(delete("/api/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Author not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors/999"));

        verify(authorService).deleteAuthor(999L);
    }


    @Test
    @DisplayName("DELETE /api/authors/{id} - should return 409 when author is associated with books")
    void deleteAuthor_shouldReturn409WhenAuthorHasBooks()
            throws Exception {

        doThrow(
                new DuplicateResourceException(
                        "Author cannot be deleted because it is associated with one or more books"
                )
        )
                .when(authorService)
                .deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Author cannot be deleted because it is associated with one or more books"
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/authors/1"));

        verify(authorService).deleteAuthor(1L);
    }
}