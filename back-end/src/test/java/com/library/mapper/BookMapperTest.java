package com.library.mapper;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.dto.response.BookResponse;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthor;
import com.library.entity.BookGenre;
import com.library.entity.Genre;
import com.library.entity.Publisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private final BookMapper mapper = new BookMapper();

    @Test
    @DisplayName("toEntity - should map create request to entity")
    void toEntity_shouldMapRequestToEntity() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Cien años de soledad")
                .isbn("9780307474728")
                .publicationYear((short) 1967)
                .edition("First")
                .language("Spanish")
                .pages((short) 417)
                .stock(10)
                .shelfLocation("A-01")
                .publisherId(1L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();

        Book result = mapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getId());

        assertEquals("Cien años de soledad", result.getTitle());
        assertEquals("9780307474728", result.getIsbn());
        assertEquals((short) 1967, result.getPublicationYear());
        assertEquals("First", result.getEdition());
        assertEquals("Spanish", result.getLanguage());
        assertEquals((short) 417, result.getPages());
        assertEquals(10, result.getStock());
        assertEquals("A-01", result.getShelfLocation());

        assertNull(result.getPublisher());
        assertNotNull(result.getAuthors());
        assertTrue(result.getAuthors().isEmpty());
        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    @DisplayName("toEntity - should return null when request is null")
    void toEntity_shouldReturnNullWhenRequestIsNull() {

        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("updateEntity - should update book fields")
    void updateEntity_shouldUpdateBook() {

        Book book = Book.builder()
                .id("A12")
                .title("Old Title")
                .isbn("OLD-ISBN")
                .publicationYear((short) 2000)
                .edition("Old Edition")
                .language("English")
                .pages((short) 100)
                .stock(5)
                .shelfLocation("OLD")
                .build();

        UpdateBookRequest request = UpdateBookRequest.builder()
                .title("New Title")
                .isbn("NEW-ISBN")
                .publicationYear((short) 2020)
                .edition("New Edition")
                .language("Spanish")
                .pages((short) 300)
                .stock(20)
                .shelfLocation("B-10")
                .publisherId(2L)
                .authorIds(List.of(2L))
                .genreIds(List.of(2L))
                .build();

        mapper.updateEntity(book, request);

        assertEquals("A12", book.getId());
        assertEquals("New Title", book.getTitle());
        assertEquals("NEW-ISBN", book.getIsbn());
        assertEquals((short) 2020, book.getPublicationYear());
        assertEquals("New Edition", book.getEdition());
        assertEquals("Spanish", book.getLanguage());
        assertEquals((short) 300, book.getPages());
        assertEquals(20, book.getStock());
        assertEquals("B-10", book.getShelfLocation());
    }

    @Test
    @DisplayName("updateEntity - should do nothing when book is null")
    void updateEntity_shouldDoNothingWhenBookIsNull() {

        UpdateBookRequest request = UpdateBookRequest.builder()
                .title("New Title")
                .build();

        assertDoesNotThrow(() ->
                mapper.updateEntity(null, request)
        );
    }

    @Test
    @DisplayName("updateEntity - should do nothing when request is null")
    void updateEntity_shouldDoNothingWhenRequestIsNull() {

        Book book = Book.builder()
                .id("A12")
                .title("Original")
                .build();

        mapper.updateEntity(book, null);

        assertEquals("Original", book.getTitle());
    }

    @Test
    @DisplayName("toResponse - should map book with publisher, authors and genres")
    void toResponse_shouldMapBook() {

        Author authorB = Author.builder()
                .id(2L)
                .name("Z Author")
                .country("Mexico")
                .build();

        Author authorA = Author.builder()
                .id(1L)
                .name("A Author")
                .country("Mexico")
                .build();

        Genre genreB = Genre.builder()
                .id(2L)
                .name("Z Genre")
                .build();

        Genre genreA = Genre.builder()
                .id(1L)
                .name("A Genre")
                .build();

        Publisher publisher = Publisher.builder()
                .id(1L)
                .name("Publisher")
                .country("Mexico")
                .build();

        Book book = Book.builder()
                .id("A12")
                .title("Book")
                .isbn("ISBN")
                .publicationYear((short) 2020)
                .stock(10)
                .shelfLocation("A-01")
                .publisher(publisher)
                .build();

        book.setAuthors(Set.of(
                BookAuthor.builder().book(book).author(authorB).build(),
                BookAuthor.builder().book(book).author(authorA).build()
        ));

        book.setGenres(Set.of(
                BookGenre.builder().book(book).genre(genreB).build(),
                BookGenre.builder().book(book).genre(genreA).build()
        ));

        BookResponse result = mapper.toResponse(book);

        assertNotNull(result);
        assertEquals("A12", result.getId());
        assertEquals("Book", result.getTitle());
        assertEquals("ISBN", result.getIsbn());
        assertEquals((short) 2020, result.getPublicationYear());
        assertEquals(10, result.getStock());
        assertEquals("A-01", result.getShelfLocation());

        assertEquals("Publisher", result.getPublisher());

        assertEquals(
                List.of("A Author", "Z Author"),
                result.getAuthors()
        );

        assertEquals(
                List.of("A Genre", "Z Genre"),
                result.getGenres()
        );
    }

    @Test
    @DisplayName("toResponse - should return null publisher when book has no publisher")
    void toResponse_shouldReturnNullPublisher() {

        Book book = Book.builder()
                .id("A12")
                .title("Book")
                .stock(5)
                .build();

        BookResponse result = mapper.toResponse(book);

        assertNotNull(result);
        assertNull(result.getPublisher());
        assertTrue(result.getAuthors().isEmpty());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    @DisplayName("toResponse - should return null when book is null")
    void toResponse_shouldReturnNullWhenBookIsNull() {

        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("toDetailResponse - should map complete book")
    void toDetailResponse_shouldMapCompleteBook() {

        Author authorB = Author.builder()
                .id(2L)
                .name("z Author")
                .country("Mexico")
                .birthDate(null)
                .build();

        Author authorA = Author.builder()
                .id(1L)
                .name("A Author")
                .country("Colombia")
                .build();

        Genre genreB = Genre.builder()
                .id(2L)
                .name("z Genre")
                .description("Z description")
                .build();

        Genre genreA = Genre.builder()
                .id(1L)
                .name("A Genre")
                .description("A description")
                .build();

        Publisher publisher = Publisher.builder()
                .id(1L)
                .name("Publisher")
                .country("Mexico")
                .foundedYear((short) 1950)
                .build();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 1, 1, 10, 0);

        LocalDateTime updatedAt =
                LocalDateTime.of(2026, 1, 2, 10, 0);

        Book book = Book.builder()
                .id("B25")
                .title("Complete Book")
                .isbn("123456")
                .publicationYear((short) 2020)
                .edition("Second")
                .language("Spanish")
                .pages((short) 500)
                .stock(15)
                .shelfLocation("C-10")
                .publisher(publisher)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        book.setAuthors(Set.of(
                BookAuthor.builder()
                        .book(book)
                        .author(authorB)
                        .build(),
                BookAuthor.builder()
                        .book(book)
                        .author(authorA)
                        .build()
        ));

        book.setGenres(Set.of(
                BookGenre.builder()
                        .book(book)
                        .genre(genreB)
                        .build(),
                BookGenre.builder()
                        .book(book)
                        .genre(genreA)
                        .build()
        ));

        BookDetailResponse result =
                mapper.toDetailResponse(book);

        assertNotNull(result);

        assertEquals("B25", result.getId());
        assertEquals("Complete Book", result.getTitle());
        assertEquals("123456", result.getIsbn());
        assertEquals((short) 2020, result.getPublicationYear());
        assertEquals("Second", result.getEdition());
        assertEquals("Spanish", result.getLanguage());
        assertEquals((short) 500, result.getPages());
        assertEquals(15, result.getStock());
        assertEquals("C-10", result.getShelfLocation());

        assertNotNull(result.getPublisher());
        assertEquals(1L, result.getPublisher().getId());
        assertEquals("Publisher", result.getPublisher().getName());
        assertEquals("Mexico", result.getPublisher().getCountry());
        assertEquals(
                Short.valueOf((short) 1950),
                result.getPublisher().getFoundedYear()
        );

        assertEquals(2, result.getAuthors().size());
        assertEquals("A Author", result.getAuthors().get(0).getName());
        assertEquals("z Author", result.getAuthors().get(1).getName());

        assertEquals(2, result.getGenres().size());
        assertEquals("A Genre", result.getGenres().get(0).getName());
        assertEquals("z Genre", result.getGenres().get(1).getName());

        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());
    }

    @Test
    @DisplayName("toDetailResponse - should return null publisher")
    void toDetailResponse_shouldReturnNullPublisher() {

        Book book = Book.builder()
                .id("A12")
                .title("Book")
                .stock(5)
                .build();

        BookDetailResponse result =
                mapper.toDetailResponse(book);

        assertNotNull(result);
        assertNull(result.getPublisher());
        assertTrue(result.getAuthors().isEmpty());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    @DisplayName("toDetailResponse - should return null when book is null")
    void toDetailResponse_shouldReturnNullWhenBookIsNull() {

        assertNull(mapper.toDetailResponse(null));
    }
}