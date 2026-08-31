package com.library.service;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.*;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.BookHistoryMapper;
import com.library.mapper.BookMapper;
import com.library.repository.*;
import com.library.service.impl.BookServiceImpl;
import com.library.util.BookIdGenerator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BookServiceTest {

    @Mock BookRepository bookRepository;
    @Mock AuthorRepository authorRepository;
    @Mock GenreRepository genreRepository;
    @Mock PublisherRepository publisherRepository;
    @Mock BookHistoryRepository historyRepository;

    @Spy BookMapper mapper = new BookMapper();
    @Spy BookHistoryMapper historyMapper = new BookHistoryMapper();

    @Mock BookIdGenerator idGenerator;
    @Mock EntityManager entityManager;

    @InjectMocks
    BookServiceImpl service;

    private Publisher publisher;
    private Author author;
    private Genre genre;

    @BeforeEach
    void init() {
        publisher = Publisher.builder()
                .id(1L)
                .name("Planeta")
                .build();

        author = Author.builder()
                .id(1L)
                .name("Frank Herbert")
                .build();

        genre = Genre.builder()
                .id(1L)
                .name("Sci-Fi")
                .build();
    }

    @Test
    void shouldCreateBookSuccessfully() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Dune")
                .isbn("978")
                .stock(10)
                .publisherId(1L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();

        when(bookRepository.existsByIsbn("978")).thenReturn(false);
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(any())).thenReturn(List.of(author));
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(idGenerator.generate()).thenReturn("A12");

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

        when(bookRepository.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        BookDetailResponse response = service.createBook(request);

        assertEquals("A12", response.getId());
        assertEquals("Dune", response.getTitle());

        Book saved = captor.getValue();

        assertEquals("A12", saved.getId());
        assertEquals("978", saved.getIsbn());
        assertEquals(1, saved.getAuthors().size());
        assertEquals(1, saved.getGenres().size());
    }

    @Test
    void shouldNormalizeBlankIsbnToNull() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Book")
                .isbn("   ")
                .stock(5)
                .publisherId(1L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(any())).thenReturn(List.of(author));
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(idGenerator.generate()).thenReturn("B10");

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

        when(bookRepository.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        service.createBook(request);

        assertNull(captor.getValue().getIsbn());

        verify(bookRepository, never())
                .existsByIsbn(any());
    }

    @Test
    void shouldThrowWhenIsbnExists() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Dune")
                .isbn("111")
                .stock(1)
                .publisherId(1L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();

        when(bookRepository.existsByIsbn("111")).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> service.createBook(request)
        );

        verify(bookRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPublisherDoesNotExist() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Book")
                .stock(1)
                .publisherId(99L)
                .authorIds(List.of(1L))
                .genreIds(List.of(1L))
                .build();

        when(publisherRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createBook(request)
        );
    }

    @Test
    void shouldThrowWhenAuthorMissing() {

        CreateBookRequest request = CreateBookRequest.builder()
                .title("Book")
                .stock(1)
                .publisherId(1L)
                .authorIds(List.of(1L, 2L))
                .genreIds(List.of(1L))
                .build();

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(any()))
                .thenReturn(List.of(author));

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createBook(request)
        );
    }

    @Test
    void shouldSearchBooksUsingNormalizedTitle() {

        Book book = Book.builder()
                .id("A01")
                .title("Dune")
                .publisher(publisher)
                .stock(1)
                .authors(Set.of())
                .genres(Set.of())
                .build();

        when(bookRepository.searchBooks("Dune", null, null))
                .thenReturn(List.of(book));

        var result = service.searchBooks(" Dune ", null, null);

        assertEquals(1, result.size());

        verify(bookRepository)
                .searchBooks("Dune", null, null);
    }

    @Test
    void shouldReturnBookById() {

        Book book = Book.builder()
                .id("A01")
                .title("Dune")
                .publisher(publisher)
                .stock(1)
                .authors(Set.of())
                .genres(Set.of())
                .build();

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        BookDetailResponse result = service.getBookById("A01");

        assertEquals("Dune", result.getTitle());
    }

    @Test
    void shouldThrowWhenBookDoesNotExist() {

        when(bookRepository.findById("X99"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getBookById("X99")
        );
    }

    @Test
    void shouldDeleteBookAndCreateHistory() {

        Book book = Book.builder()
                .id("A01")
                .title("Dune")
                .publisher(publisher)
                .stock(1)
                .authors(Set.of())
                .genres(Set.of())
                .build();

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        service.deleteBook("A01");

        verify(historyRepository).save(any());
        verify(bookRepository).delete(book);
    }
}
