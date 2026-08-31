package com.library.service.impl;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.dto.response.BookResponse;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthor;
import com.library.entity.BookGenre;
import com.library.entity.BookHistory;
import com.library.entity.Genre;
import com.library.entity.Publisher;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.BookHistoryMapper;
import com.library.mapper.BookMapper;
import com.library.repository.AuthorRepository;
import com.library.repository.BookHistoryRepository;
import com.library.repository.BookRepository;
import com.library.repository.GenreRepository;
import com.library.repository.PublisherRepository;
import com.library.util.BookIdGenerator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookHistoryRepository bookHistoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookHistoryMapper bookHistoryMapper;

    @Mock
    private BookIdGenerator bookIdGenerator;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private Author author;
    private Genre genre;
    private Publisher publisher;

    @BeforeEach
    void setUp() {

        author = Author.builder()
                .id(1L)
                .name("Gabriel García Márquez")
                .build();

        genre = Genre.builder()
                .id(1L)
                .name("Novela")
                .build();

        publisher = Publisher.builder()
                .id(1L)
                .name("Editorial Test")
                .build();

        book = Book.builder()
                .id("A01")
                .title("Cien años de soledad")
                .isbn("9780307474728")
                .publisher(publisher)
                .authors(new HashSet<>())
                .genres(new HashSet<>())
                .build();
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void createBook_shouldCreateBookSuccessfully() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(" 9780307474728 ");

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L));

        when(bookRepository.existsByIsbn("9780307474728"))
                .thenReturn(false);

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(genre));

        when(bookMapper.toEntity(request))
                .thenReturn(book);

        when(bookIdGenerator.generate())
                .thenReturn("A01");

        when(bookRepository.save(book))
                .thenReturn(book);

        BookDetailResponse response =
                mock(BookDetailResponse.class);

        when(bookMapper.toDetailResponse(book))
                .thenReturn(response);

        BookDetailResponse result =
                bookService.createBook(request);

        assertSame(response, result);

        assertEquals("A01", book.getId());
        assertEquals(
                "9780307474728",
                book.getIsbn()
        );
        assertEquals(
                publisher,
                book.getPublisher()
        );

        assertEquals(
                1,
                book.getAuthors().size()
        );

        assertEquals(
                1,
                book.getGenres().size()
        );

        verify(bookRepository)
                .existsByIsbn("9780307474728");

        verify(publisherRepository)
                .findById(1L);

        verify(authorRepository)
                .findAllById(List.of(1L));

        verify(genreRepository)
                .findAllById(List.of(1L));

        verify(bookRepository)
                .save(book);

        verify(bookMapper)
                .toDetailResponse(book);
    }

    @Test
    void createBook_shouldAllowNullIsbn() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(genre));

        when(bookMapper.toEntity(request))
                .thenReturn(book);

        when(bookIdGenerator.generate())
                .thenReturn("A01");

        when(bookRepository.save(book))
                .thenReturn(book);

        BookDetailResponse response =
                mock(BookDetailResponse.class);

        when(bookMapper.toDetailResponse(book))
                .thenReturn(response);

        BookDetailResponse result =
                bookService.createBook(request);

        assertSame(response, result);
        assertNull(book.getIsbn());

        verify(bookRepository, never())
                .existsByIsbn(anyString());

        verify(bookRepository)
                .save(book);
    }

    @Test
    void createBook_shouldNormalizeBlankIsbnToNull() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn("   ");

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(genre));

        when(bookMapper.toEntity(request))
                .thenReturn(book);

        when(bookIdGenerator.generate())
                .thenReturn("A01");

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookMapper.toDetailResponse(book))
                .thenReturn(
                        mock(BookDetailResponse.class)
                );

        bookService.createBook(request);

        assertNull(book.getIsbn());

        verify(bookRepository, never())
                .existsByIsbn(anyString());
    }

    @Test
    void createBook_shouldThrowExceptionWhenIsbnAlreadyExists() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(" 9780307474728 ");

        when(bookRepository.existsByIsbn("9780307474728"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> bookService.createBook(request)
        );

        verify(bookRepository)
                .existsByIsbn("9780307474728");

        verifyNoInteractions(
                publisherRepository,
                authorRepository,
                genreRepository,
                bookMapper,
                bookIdGenerator
        );

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void createBook_shouldThrowExceptionWhenPublisherDoesNotExist() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(99L);

        when(publisherRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.createBook(request)
        );

        verify(publisherRepository)
                .findById(99L);

        verifyNoInteractions(
                authorRepository,
                genreRepository,
                bookMapper,
                bookIdGenerator
        );

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void createBook_shouldThrowExceptionWhenAuthorDoesNotExist() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L, 2L));

        /*
         * No necesitamos stub de genreIds.
         *
         * La ejecución termina en validateAuthors()
         * antes de consultar los géneros.
         */

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(
                List.of(1L, 2L)
        )).thenReturn(List.of(author));

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.createBook(request)
        );

        verify(publisherRepository)
                .findById(1L);

        verify(authorRepository)
                .findAllById(List.of(1L, 2L));

        verify(genreRepository, never())
                .findAllById(anyList());

        verify(bookMapper, never())
                .toEntity(any());

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void createBook_shouldThrowExceptionWhenGenreDoesNotExist() {

        CreateBookRequest request =
                mock(CreateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L, 2L));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(
                List.of(1L, 2L)
        )).thenReturn(List.of(genre));

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.createBook(request)
        );

        verify(genreRepository)
                .findAllById(List.of(1L, 2L));

        verify(bookRepository, never())
                .save(any());
    }

    // =========================================================
    // SEARCH
    // =========================================================

    @Test
    void searchBooks_shouldReturnMappedBooks() {

        Book book2 = Book.builder()
                .id("B02")
                .title("El amor en los tiempos del cólera")
                .build();

        BookResponse response1 =
                mock(BookResponse.class);

        BookResponse response2 =
                mock(BookResponse.class);

        when(bookRepository.searchBooks(
                "cien",
                List.of(1L),
                List.of(1L)
        )).thenReturn(List.of(book, book2));

        when(bookMapper.toResponse(book))
                .thenReturn(response1);

        when(bookMapper.toResponse(book2))
                .thenReturn(response2);

        List<BookResponse> result =
                bookService.searchBooks(
                        " cien ",
                        List.of(1L),
                        List.of(1L)
                );

        assertEquals(2, result.size());

        assertSame(
                response1,
                result.get(0)
        );

        assertSame(
                response2,
                result.get(1)
        );

        /*
         * El service normaliza el título antes de
         * enviarlo al repository.
         */
        verify(bookRepository)
                .searchBooks(
                        "cien",
                        List.of(1L),
                        List.of(1L)
                );
    }

    @Test
    void searchBooks_shouldNormalizeBlankTitleAndEmptyIds() {

        when(bookRepository.searchBooks(
                "",
                null,
                null
        )).thenReturn(List.of());

        List<BookResponse> result =
                bookService.searchBooks(
                        "   ",
                        List.of(),
                        null
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository)
                .searchBooks(
                        "",
                        null,
                        null
                );
    }

    @Test
    void searchBooks_shouldNormalizeNullTitle() {

        when(bookRepository.searchBooks(
                "",
                null,
                null
        )).thenReturn(List.of());

        List<BookResponse> result =
                bookService.searchBooks(
                        null,
                        null,
                        null
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository)
                .searchBooks(
                        "",
                        null,
                        null
                );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getBookById_shouldReturnBook() {

        BookDetailResponse response =
                mock(BookDetailResponse.class);

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(bookMapper.toDetailResponse(book))
                .thenReturn(response);

        BookDetailResponse result =
                bookService.getBookById("A01");

        assertSame(response, result);

        verify(bookRepository)
                .findById("A01");

        verify(bookMapper)
                .toDetailResponse(book);
    }

    @Test
    void getBookById_shouldThrowExceptionWhenBookDoesNotExist() {

        when(bookRepository.findById("A99"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getBookById("A99")
        );

        verify(bookRepository)
                .findById("A99");

        verifyNoInteractions(bookMapper);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void updateBook_shouldUpdateBookSuccessfully() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(" 9999999999999 ");

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L));

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(bookRepository.existsByIsbn(
                "9999999999999"
        )).thenReturn(false);

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(genre));

        doNothing()
                .when(bookMapper)
                .updateEntity(book, request);

        when(bookRepository.save(book))
                .thenReturn(book);

        BookDetailResponse response =
                mock(BookDetailResponse.class);

        when(bookMapper.toDetailResponse(book))
                .thenReturn(response);

        BookDetailResponse result =
                bookService.updateBook(
                        "A01",
                        request
                );

        assertSame(response, result);

        assertEquals(
                "9999999999999",
                book.getIsbn()
        );

        assertEquals(
                publisher,
                book.getPublisher()
        );

        assertEquals(
                1,
                book.getAuthors().size()
        );

        assertEquals(
                1,
                book.getGenres().size()
        );

        verify(bookRepository)
                .findById("A01");

        verify(bookRepository)
                .existsByIsbn("9999999999999");

        verify(publisherRepository)
                .findById(1L);

        verify(authorRepository)
                .findAllById(List.of(1L));

        verify(genreRepository)
                .findAllById(List.of(1L));

        verify(bookMapper)
                .updateEntity(book, request);

        verify(entityManager, times(2))
                .flush();

        verify(bookRepository)
                .save(book);

        verify(bookMapper)
                .toDetailResponse(book);
    }

    @Test
    void updateBook_shouldAllowSameIsbn() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(" 9780307474728 ");

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L));

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(genre));

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookMapper.toDetailResponse(book))
                .thenReturn(
                        mock(BookDetailResponse.class)
                );

        bookService.updateBook(
                "A01",
                request
        );

        verify(bookRepository, never())
                .existsByIsbn(anyString());

        verify(bookRepository)
                .save(book);

        assertEquals(
                "9780307474728",
                book.getIsbn()
        );
    }

    @Test
    void updateBook_shouldThrowExceptionWhenNewIsbnAlreadyExists() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(" 1111111111111 ");

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(bookRepository.existsByIsbn(
                "1111111111111"
        )).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> bookService.updateBook(
                        "A01",
                        request
                )
        );

        verify(bookRepository)
                .findById("A01");

        verify(bookRepository)
                .existsByIsbn("1111111111111");

        verifyNoInteractions(
                publisherRepository,
                authorRepository,
                genreRepository,
                bookMapper,
                entityManager
        );

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void updateBook_shouldAllowNullIsbn() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L));

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(genre));

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookMapper.toDetailResponse(book))
                .thenReturn(
                        mock(BookDetailResponse.class)
                );

        bookService.updateBook(
                "A01",
                request
        );

        assertNull(book.getIsbn());

        verify(bookRepository, never())
                .existsByIsbn(anyString());

        verify(bookRepository)
                .save(book);
    }

    @Test
    void updateBook_shouldThrowExceptionWhenBookDoesNotExist() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(bookRepository.findById("A99"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.updateBook(
                        "A99",
                        request
                )
        );

        verify(bookRepository)
                .findById("A99");

        verifyNoInteractions(
                publisherRepository,
                authorRepository,
                genreRepository,
                bookMapper,
                entityManager
        );

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void updateBook_shouldThrowExceptionWhenPublisherDoesNotExist() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        /*
         * Solo necesitamos stubear lo que el service
         * realmente ejecuta antes de lanzar la excepción.
         */
        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(99L);

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(publisherRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.updateBook(
                        "A01",
                        request
                )
        );

        verify(bookRepository)
                .findById("A01");

        verify(publisherRepository)
                .findById(99L);

        verifyNoInteractions(
                authorRepository,
                genreRepository,
                bookMapper,
                entityManager
        );

        verify(bookRepository, never())
                .existsByIsbn(anyString());

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void updateBook_shouldThrowExceptionWhenAuthorDoesNotExist() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L, 2L));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(authorRepository.findAllById(
                List.of(1L, 2L)
        )).thenReturn(List.of(author));

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.updateBook(
                        "A01",
                        request
                )
        );

        verify(bookRepository)
                .findById("A01");

        verify(publisherRepository)
                .findById(1L);

        verify(authorRepository)
                .findAllById(List.of(1L, 2L));

        /*
         * La excepción ocurre en validateAuthors(),
         * por lo que nunca se consulta genreRepository.
         */
        verify(genreRepository, never())
                .findAllById(anyList());

        verify(bookMapper, never())
                .updateEntity(any(), any());

        verify(entityManager, never())
                .flush();

        verify(bookRepository, never())
                .save(any());
    }

    @Test
    void updateBook_shouldThrowExceptionWhenGenreDoesNotExist() {

        UpdateBookRequest request =
                mock(UpdateBookRequest.class);

        when(request.getIsbn())
                .thenReturn(null);

        when(request.getPublisherId())
                .thenReturn(1L);

        when(request.getAuthorIds())
                .thenReturn(List.of(1L));

        when(request.getGenreIds())
                .thenReturn(List.of(1L, 2L));

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(authorRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(author));

        when(genreRepository.findAllById(
                List.of(1L, 2L)
        )).thenReturn(List.of(genre));

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.updateBook(
                        "A01",
                        request
                )
        );

        verify(genreRepository)
                .findAllById(List.of(1L, 2L));

        verify(bookRepository, never())
                .save(any());

        verify(bookMapper, never())
                .updateEntity(any(), any());

        verify(entityManager, never())
                .flush();
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void deleteBook_shouldCreateHistoryAndDeleteBook() {

        BookHistory history =
                mock(BookHistory.class);

        when(bookRepository.findById("A01"))
                .thenReturn(Optional.of(book));

        when(bookHistoryMapper.toHistory(
                book,
                null
        )).thenReturn(history);

        when(bookHistoryRepository.save(history))
                .thenReturn(history);

        bookService.deleteBook("A01");

        verify(bookRepository)
                .findById("A01");

        verify(bookHistoryMapper)
                .toHistory(
                        book,
                        null
                );

        verify(bookHistoryRepository)
                .save(history);

        verify(bookRepository)
                .delete(book);
    }

    @Test
    void deleteBook_shouldThrowExceptionWhenBookDoesNotExist() {

        when(bookRepository.findById("A99"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.deleteBook("A99")
        );

        verify(bookRepository)
                .findById("A99");

        verifyNoInteractions(
                bookHistoryMapper,
                bookHistoryRepository
        );

        verify(bookRepository, never())
                .delete(any());
    }
}