package com.library.mapper;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.entity.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private final BookMapper mapper = new BookMapper();

    @Test
    void shouldConvertRequestToEntity() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Dune")
                .isbn("123")
                .stock(5)
                .build();

        Book book = mapper.toEntity(request);

        assertEquals("Dune", book.getTitle());
        assertEquals("123", book.getIsbn());
        assertEquals(5, book.getStock());
    }

    @Test
    void shouldUpdateEntity() {

        Book book = Book.builder()
                .title("Old")
                .stock(1)
                .build();

        UpdateBookRequest request = UpdateBookRequest.builder()
                .title("New")
                .stock(9)
                .build();

        mapper.updateEntity(book, request);

        assertEquals("New", book.getTitle());
        assertEquals(9, book.getStock());
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {

        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldIgnoreNullUpdate() {

        Book book = Book.builder()
                .title("Dune")
                .build();

        mapper.updateEntity(book, null);

        assertEquals("Dune", book.getTitle());
    }
}