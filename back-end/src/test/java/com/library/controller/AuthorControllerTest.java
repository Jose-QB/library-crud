package com.library.controller;

import com.library.config.SecurityConfig;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;
import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import com.library.security.CustomUserDetailsService;
import com.library.security.JwtService;
import com.library.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAuthors_shouldReturn200() throws Exception {

        AuthorResponse author = AuthorResponse.builder()
                .id(1L)
                .name("J. R. R. Tolkien")
                .country("United Kingdom")
                .birthDate(LocalDate.of(1892, 1, 3))
                .build();

        when(authorService.getAllAuthors())
                .thenReturn(List.of(author));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("J. R. R. Tolkien"))
                .andExpect(jsonPath("$[0].country")
                        .value("United Kingdom"))
                .andExpect(jsonPath("$[0].birthDate")
                        .value("1892-01-03"));

        verify(authorService).getAllAuthors();
    }

    @Test
    void getAuthors_shouldReturnEmptyList() throws Exception {

        when(authorService.getAllAuthors())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(authorService).getAllAuthors();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getAuthor_shouldReturn200WhenAuthorExists() throws Exception {

        AuthorResponse author = AuthorResponse.builder()
                .id(1L)
                .name("George Orwell")
                .country("United Kingdom")
                .birthDate(LocalDate.of(1903, 6, 25))
                .build();

        when(authorService.getAuthorById(1L))
                .thenReturn(author);

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("George Orwell"))
                .andExpect(jsonPath("$.country")
                        .value("United Kingdom"))
                .andExpect(jsonPath("$.birthDate")
                        .value("1903-06-25"));

        verify(authorService).getAuthorById(1L);
    }

    @Test
    void getAuthor_shouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        when(authorService.getAuthorById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Author with ID 999 not found"
                ));

        mockMvc.perform(get("/api/authors/999"))
                .andExpect(status().isNotFound());

        verify(authorService).getAuthorById(999L);
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    @WithMockUser(roles = "USER")
    void createAuthor_shouldReturn201ForUser() throws Exception {

        CreateAuthorRequest request =
                CreateAuthorRequest.builder()
                        .name("Isaac Asimov")
                        .country("United States")
                        .birthDate(LocalDate.of(1920, 1, 2))
                        .build();

        AuthorResponse response =
                AuthorResponse.builder()
                        .id(5L)
                        .name("Isaac Asimov")
                        .country("United States")
                        .birthDate(LocalDate.of(1920, 1, 2))
                        .build();

        when(authorService.createAuthor(any(CreateAuthorRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name")
                        .value("Isaac Asimov"))
                .andExpect(jsonPath("$.country")
                        .value("United States"))
                .andExpect(jsonPath("$.birthDate")
                        .value("1920-01-02"));

        verify(authorService)
                .createAuthor(any(CreateAuthorRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAuthor_shouldReturn201ForAdmin() throws Exception {

        CreateAuthorRequest request =
                CreateAuthorRequest.builder()
                        .name("Isaac Asimov")
                        .build();

        AuthorResponse response =
                AuthorResponse.builder()
                        .id(5L)
                        .name("Isaac Asimov")
                        .build();

        when(authorService.createAuthor(any(CreateAuthorRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name")
                        .value("Isaac Asimov"));

        verify(authorService)
                .createAuthor(any(CreateAuthorRequest.class));
    }

    @Test
    void createAuthor_shouldReturn401WithoutAuthentication()
            throws Exception {

        CreateAuthorRequest request =
                CreateAuthorRequest.builder()
                        .name("Isaac Asimov")
                        .build();

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void createAuthor_shouldReturn403ForUnauthorizedRole()
            throws Exception {

        CreateAuthorRequest request =
                CreateAuthorRequest.builder()
                        .name("Isaac Asimov")
                        .build();

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAuthor_shouldReturn400ForInvalidRequest()
            throws Exception {

        CreateAuthorRequest request =
                CreateAuthorRequest.builder()
                        .name("")
                        .build();

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAuthor_shouldReturn409WhenAuthorAlreadyExists()
            throws Exception {

        CreateAuthorRequest request =
                CreateAuthorRequest.builder()
                        .name("George Orwell")
                        .build();

        when(authorService.createAuthor(any(CreateAuthorRequest.class)))
                .thenThrow(new DuplicateResourceException(
                        "An author with name 'George Orwell' already exists"
                ));

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(authorService)
                .createAuthor(any(CreateAuthorRequest.class));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    @WithMockUser(roles = "USER")
    void updateAuthor_shouldReturn200ForUser() throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("George Orwell")
                        .country("United Kingdom")
                        .birthDate(LocalDate.of(1903, 6, 25))
                        .build();

        AuthorResponse response =
                AuthorResponse.builder()
                        .id(1L)
                        .name("George Orwell")
                        .country("United Kingdom")
                        .birthDate(LocalDate.of(1903, 6, 25))
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
                        .value("George Orwell"));

        verify(authorService)
                .updateAuthor(
                        eq(1L),
                        any(UpdateAuthorRequest.class)
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAuthor_shouldReturn200ForAdmin() throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("George Orwell")
                        .build();

        AuthorResponse response =
                AuthorResponse.builder()
                        .id(1L)
                        .name("George Orwell")
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
                        .value("George Orwell"));

        verify(authorService)
                .updateAuthor(
                        eq(1L),
                        any(UpdateAuthorRequest.class)
                );
    }

    @Test
    void updateAuthor_shouldReturn401WithoutAuthentication()
            throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("George Orwell")
                        .build();

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void updateAuthor_shouldReturn403ForUnauthorizedRole()
            throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("George Orwell")
                        .build();

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateAuthor_shouldReturn400ForInvalidRequest()
            throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("")
                        .build();

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateAuthor_shouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("New Author")
                        .build();

        when(authorService.updateAuthor(
                eq(999L),
                any(UpdateAuthorRequest.class)
        )).thenThrow(new ResourceNotFoundException(
                "Author with ID 999 not found"
        ));

        mockMvc.perform(
                        put("/api/authors/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(authorService)
                .updateAuthor(
                        eq(999L),
                        any(UpdateAuthorRequest.class)
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateAuthor_shouldReturn409WhenNameAlreadyExists()
            throws Exception {

        UpdateAuthorRequest request =
                UpdateAuthorRequest.builder()
                        .name("J. R. R. Tolkien")
                        .build();

        when(authorService.updateAuthor(
                eq(1L),
                any(UpdateAuthorRequest.class)
        )).thenThrow(new DuplicateResourceException(
                "An author with name 'J. R. R. Tolkien' already exists"
        ));

        mockMvc.perform(
                        put("/api/authors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(authorService)
                .updateAuthor(
                        eq(1L),
                        any(UpdateAuthorRequest.class)
                );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAuthor_shouldReturn204ForAdmin() throws Exception {

        doNothing()
                .when(authorService)
                .deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());

        verify(authorService)
                .deleteAuthor(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteAuthor_shouldReturn403ForUser() throws Exception {

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authorService);
    }

    @Test
    void deleteAuthor_shouldReturn401WithoutAuthentication()
            throws Exception {

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authorService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAuthor_shouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        doThrow(new ResourceNotFoundException(
                "Author with ID 999 not found"
        ))
                .when(authorService)
                .deleteAuthor(999L);

        mockMvc.perform(delete("/api/authors/999"))
                .andExpect(status().isNotFound());

        verify(authorService)
                .deleteAuthor(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAuthor_shouldReturn409WhenAuthorHasBooks()
            throws Exception {

        doThrow(new DuplicateResourceException(
                "Cannot delete author with ID 1 because it is associated with one or more books"
        ))
                .when(authorService)
                .deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isConflict());

        verify(authorService)
                .deleteAuthor(1L);
    }
}

