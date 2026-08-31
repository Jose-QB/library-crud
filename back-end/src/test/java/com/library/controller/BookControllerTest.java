package com.library.controller;

import com.library.config.SecurityConfig;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;
import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.repository.UserRepository;
import com.library.security.CustomUserDetailsService;
import com.library.security.JwtService;
import com.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    BookService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldCreateBook() throws Exception {

        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Dune");
        request.setPublisherId(1L);
        request.setAuthorIds(List.of(1L));
        request.setGenreIds(List.of(1L));

        BookDetailResponse response = new BookDetailResponse();
        response.setId("A12");
        response.setTitle("Dune");

        when(service.createBook(any()))
                .thenReturn(response);

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("A12"))
                .andExpect(jsonPath("$.title").value("Dune"));
    }

    @Test
    void shouldGetBookById() throws Exception {

        BookDetailResponse response = new BookDetailResponse();
        response.setId("B22");
        response.setTitle("1984");

        when(service.getBookById("B22"))
                .thenReturn(response);

        mvc.perform(get("/api/books/B22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"));
    }

    @Test
    void shouldSearchBooks() throws Exception {

        when(service.searchBooks(any(), any(), any()))
                .thenReturn(List.of());

        mvc.perform(get("/api/books")
                        .param("title", "Harry"))
                .andExpect(status().isOk());

        verify(service).searchBooks(
                eq("Harry"),
                isNull(),
                isNull()
        );
    }

    @Test
    void shouldUpdateBook() throws Exception {

        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Nuevo");

        BookDetailResponse response = new BookDetailResponse();
        response.setId("C10");
        response.setTitle("Nuevo");

        when(service.updateBook(eq("C10"), any()))
                .thenReturn(response);

        mvc.perform(put("/api/books/C10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nuevo"));
    }

    @Test
    void shouldDeleteBook() throws Exception {

        doNothing().when(service).deleteBook("D99");

        mvc.perform(delete("/api/books/D99"))
                .andExpect(status().isNoContent());

        verify(service).deleteBook("D99");
    }
}
