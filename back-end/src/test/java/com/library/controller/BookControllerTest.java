package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.dto.response.BookResponse;
import com.library.exception.DuplicateResourceException;
import com.library.exception.GlobalExceptionHandler;
import com.library.exception.ResourceNotFoundException;
import com.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    // ============================================================
    // CREATE
    // ============================================================

    @Test
    void shouldCreateBookSuccessfully() throws Exception {

        CreateBookRequest request = validCreateBookRequest();

        BookDetailResponse response = BookDetailResponse.builder()
                .id("A12")
                .title("Clean Code")
                .isbn("9780132350884")
                .publicationYear((short) 2008)
                .edition("1st")
                .language("English")
                .pages((short) 464)
                .stock(10)
                .shelfLocation("A-01")
                .build();

        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A12"))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.shelfLocation").value("A-01"));

        verify(bookService).createBook(any(CreateBookRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreateBookRequestIsInvalid() throws Exception {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("")
                .stock(0)
                .publisherId(null)
                .authorIds(List.of())
                .genreIds(List.of())
                .build();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoInteractions(bookService);
    }

    @Test
    void shouldReturnNotFoundWhenReferencedResourceDoesNotExistOnCreate()
            throws Exception {

        CreateBookRequest request = validCreateBookRequest();

        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Author not found"
                ));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Author not found"));

        verify(bookService).createBook(any(CreateBookRequest.class));
    }

    @Test
    void shouldReturnConflictWhenBookAlreadyExists() throws Exception {

        CreateBookRequest request = validCreateBookRequest();

        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenThrow(new DuplicateResourceException(
                        "Book already exists"
                ));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Book already exists"));

        verify(bookService).createBook(any(CreateBookRequest.class));
    }

    // ============================================================
    // GET ALL / SEARCH
    // ============================================================

    @Test
    void shouldGetBooksSuccessfully() throws Exception {

        when(bookService.searchBooks(
                null,
                null,
                null
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookService).searchBooks(
                null,
                null,
                null
        );
    }

    @Test
    void shouldSearchBooksByTitle() throws Exception {

        when(bookService.searchBooks(
                eq("Clean Code"),
                eq(null),
                eq(null)
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/books")
                        .param("title", "Clean Code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookService).searchBooks(
                eq("Clean Code"),
                eq(null),
                eq(null)
        );
    }

    @Test
    void shouldSearchBooksByAuthorIds() throws Exception {

        when(bookService.searchBooks(
                eq(null),
                eq(List.of(1L, 2L)),
                eq(null)
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/books")
                        .param("authorIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookService).searchBooks(
                eq(null),
                eq(List.of(1L, 2L)),
                eq(null)
        );
    }

    @Test
    void shouldSearchBooksByGenreIds() throws Exception {

        when(bookService.searchBooks(
                eq(null),
                eq(null),
                eq(List.of(3L, 4L))
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/books")
                        .param("genreIds", "3", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookService).searchBooks(
                eq(null),
                eq(null),
                eq(List.of(3L, 4L))
        );
    }

    @Test
    void shouldSearchBooksUsingAllFilters() throws Exception {

        when(bookService.searchBooks(
                eq("Clean Code"),
                eq(List.of(1L, 2L)),
                eq(List.of(3L, 4L))
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/books")
                        .param("title", "Clean Code")
                        .param("authorIds", "1", "2")
                        .param("genreIds", "3", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookService).searchBooks(
                eq("Clean Code"),
                eq(List.of(1L, 2L)),
                eq(List.of(3L, 4L))
        );
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Test
    void shouldGetBookByIdSuccessfully() throws Exception {

        BookDetailResponse response = BookDetailResponse.builder()
                .id("A12")
                .title("Clean Code")
                .isbn("9780132350884")
                .stock(10)
                .build();

        when(bookService.getBookById("A12"))
                .thenReturn(response);

        mockMvc.perform(get("/api/books/A12"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A12"))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.stock").value(10));

        verify(bookService).getBookById("A12");
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {

        when(bookService.getBookById("A99"))
                .thenThrow(new ResourceNotFoundException(
                        "Book not found"
                ));

        mockMvc.perform(get("/api/books/A99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Book not found"));

        verify(bookService).getBookById("A99");
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Test
    void shouldUpdateBookSuccessfully() throws Exception {

        UpdateBookRequest request = validUpdateBookRequest();

        BookDetailResponse response = BookDetailResponse.builder()
                .id("A12")
                .title("Clean Code Updated")
                .isbn("9780132350884")
                .stock(0)
                .shelfLocation("B-05")
                .build();

        when(bookService.updateBook(
                eq("A12"),
                any(UpdateBookRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/books/A12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A12"))
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.stock").value(0))
                .andExpect(jsonPath("$.shelfLocation").value("B-05"));

        verify(bookService).updateBook(
                eq("A12"),
                any(UpdateBookRequest.class)
        );
    }

    @Test
    void shouldAllowZeroStockWhenUpdatingBook() throws Exception {

        UpdateBookRequest request = validUpdateBookRequest();
        request.setStock(0);

        BookDetailResponse response = BookDetailResponse.builder()
                .id("A12")
                .title("Clean Code")
                .stock(0)
                .build();

        when(bookService.updateBook(
                eq("A12"),
                any(UpdateBookRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/books/A12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0));

        verify(bookService).updateBook(
                eq("A12"),
                any(UpdateBookRequest.class)
        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateBookRequestIsInvalid()
            throws Exception {

        UpdateBookRequest request = UpdateBookRequest.builder()
                .title("")
                .stock(-1)
                .publisherId(null)
                .authorIds(List.of())
                .genreIds(List.of())
                .build();

        mockMvc.perform(put("/api/books/A12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoInteractions(bookService);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingBook()
            throws Exception {

        UpdateBookRequest request = validUpdateBookRequest();

        when(bookService.updateBook(
                eq("A99"),
                any(UpdateBookRequest.class)
        )).thenThrow(new ResourceNotFoundException(
                "Book not found"
        ));

        mockMvc.perform(put("/api/books/A99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Book not found"));

        verify(bookService).updateBook(
                eq("A99"),
                any(UpdateBookRequest.class)
        );
    }

    @Test
    void shouldReturnConflictWhenUpdatingWithExistingIsbn()
            throws Exception {

        UpdateBookRequest request = validUpdateBookRequest();

        when(bookService.updateBook(
                eq("A12"),
                any(UpdateBookRequest.class)
        )).thenThrow(new DuplicateResourceException(
                "ISBN already exists"
        ));

        mockMvc.perform(put("/api/books/A12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("ISBN already exists"));

        verify(bookService).updateBook(
                eq("A12"),
                any(UpdateBookRequest.class)
        );
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Test
    void shouldDeleteBookSuccessfully() throws Exception {

        doNothing().when(bookService).deleteBook("A12");

        mockMvc.perform(delete("/api/books/A12"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(bookService).deleteBook("A12");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingBook()
            throws Exception {

        doThrow(new ResourceNotFoundException(
                "Book not found"
        )).when(bookService).deleteBook("A99");

        mockMvc.perform(delete("/api/books/A99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Book not found"));

        verify(bookService).deleteBook("A99");
    }

    // ============================================================
    // TEST DATA
    // ============================================================

    private CreateBookRequest validCreateBookRequest() {

        return CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .publicationYear((short) 2008)
                .edition("1st")
                .language("English")
                .pages((short) 464)
                .stock(10)
                .shelfLocation("A-01")
                .publisherId(1L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();
    }

    private UpdateBookRequest validUpdateBookRequest() {

        return UpdateBookRequest.builder()
                .title("Clean Code Updated")
                .isbn("9780132350884")
                .publicationYear((short) 2008)
                .edition("2nd")
                .language("English")
                .pages((short) 464)
                .stock(10)
                .shelfLocation("B-05")
                .publisherId(1L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();
    }
}