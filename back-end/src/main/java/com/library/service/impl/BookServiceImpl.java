package com.library.service.impl;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.dto.response.BookResponse;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthor;
import com.library.entity.BookAuthorId;
import com.library.entity.BookGenre;
import com.library.entity.BookGenreId;
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
import com.library.service.BookService;
import com.library.util.BookIdGenerator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;
    private final BookHistoryRepository bookHistoryRepository;

    private final BookMapper bookMapper;
    private final BookHistoryMapper bookHistoryMapper;
    private final BookIdGenerator bookIdGenerator;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public BookDetailResponse createBook(
            CreateBookRequest request) {

        /*
         * Normalize ISBN before validation.
         *
         * null, "" and blank values are stored as null.
         */
        String isbn = normalizeIsbn(
                request.getIsbn()
        );

        /*
         * Only check duplicate ISBN when an ISBN
         * was actually provided.
         */
        if (isbn != null
                && bookRepository.existsByIsbn(isbn)) {

            throw new DuplicateResourceException(
                    "A book with ISBN '" + isbn
                            + "' already exists"
            );
        }

        Publisher publisher =
                publisherRepository.findById(
                                request.getPublisherId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Publisher with ID "
                                                + request.getPublisherId()
                                                + " not found"
                                )
                        );

        List<Author> authors =
                authorRepository.findAllById(
                        request.getAuthorIds()
                );

        validateAuthors(
                request.getAuthorIds(),
                authors
        );

        List<Genre> genres =
                genreRepository.findAllById(
                        request.getGenreIds()
                );

        validateGenres(
                request.getGenreIds(),
                genres
        );

        Book book =
                bookMapper.toEntity(request);

        book.setId(
                bookIdGenerator.generate()
        );

        /*
         * Store the normalized ISBN.
         */
        book.setIsbn(isbn);

        book.setPublisher(publisher);

        /*
         * Create book-author relationships.
         */
        Set<BookAuthor> bookAuthors =
                authors.stream()
                        .map(author ->
                                createBookAuthor(
                                        book,
                                        author
                                )
                        )
                        .collect(Collectors.toSet());

        /*
         * Create book-genre relationships.
         */
        Set<BookGenre> bookGenres =
                genres.stream()
                        .map(genre ->
                                createBookGenre(
                                        book,
                                        genre
                                )
                        )
                        .collect(Collectors.toSet());

        book.setAuthors(bookAuthors);
        book.setGenres(bookGenres);

        Book savedBook =
                bookRepository.save(book);

        return bookMapper.toDetailResponse(
                savedBook
        );
    }

    @Override
    @Transactional
    public List<BookResponse> searchBooks(
            String title,
            List<Long> authorIds,
            List<Long> genreIds) {

        return bookRepository.searchBooks(
                        normalizeTitle(title),
                        normalizeIds(authorIds),
                        normalizeIds(genreIds)
                )
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookDetailResponse getBookById(
            String id) {

        Book book = findBook(id);

        return bookMapper.toDetailResponse(
                book
        );
    }

    @Override
    @Transactional
    public BookDetailResponse updateBook(
            String id,
            UpdateBookRequest request) {

        Book book = findBook(id);

        /*
         * Normalize ISBN.
         *
         * null, "" and blank values become null.
         */
        String isbn =
                normalizeIsbn(
                        request.getIsbn()
                );

        /*
         * Validate ISBN uniqueness only when
         * an actual ISBN was provided.
         */
        if (isbn != null
                && !isbn.equals(book.getIsbn())
                && bookRepository.existsByIsbn(isbn)) {

            throw new DuplicateResourceException(
                    "A book with ISBN '" + isbn
                            + "' already exists"
            );
        }

        Publisher publisher =
                publisherRepository.findById(
                                request.getPublisherId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Publisher with ID "
                                                + request.getPublisherId()
                                                + " not found"
                                )
                        );

        List<Author> authors =
                authorRepository.findAllById(
                        request.getAuthorIds()
                );

        validateAuthors(
                request.getAuthorIds(),
                authors
        );

        List<Genre> genres =
                genreRepository.findAllById(
                        request.getGenreIds()
                );

        validateGenres(
                request.getGenreIds(),
                genres
        );

        /*
         * Update normal book fields.
         */
        bookMapper.updateEntity(
                book,
                request
        );

        /*
         * Explicitly use the normalized ISBN.
         */
        book.setIsbn(isbn);

        book.setPublisher(publisher);

        /*
         * --------------------------------------------------
         * Update authors
         * --------------------------------------------------
         *
         * Because Book uses orphanRemoval = true,
         * clearing the collection marks the old
         * BookAuthor entities for deletion.
         */
        book.getAuthors().clear();

        /*
         * Force Hibernate to synchronize those deletions
         * before creating new BookAuthor entities.
         *
         * This prevents two different BookAuthor objects
         * with the same composite identifier from being
         * managed simultaneously.
         */
        entityManager.flush();

        /*
         * Add the new relationships.
         */
        Set<BookAuthor> bookAuthors =
                authors.stream()
                        .map(author ->
                                createBookAuthor(
                                        book,
                                        author
                                )
                        )
                        .collect(Collectors.toSet());

        book.getAuthors().addAll(
                bookAuthors
        );

        /*
         * --------------------------------------------------
         * Update genres
         * --------------------------------------------------
         */
        book.getGenres().clear();

        /*
         * Flush the orphan removals before creating
         * new BookGenre entities.
         */
        entityManager.flush();

        Set<BookGenre> bookGenres =
                genres.stream()
                        .map(genre ->
                                createBookGenre(
                                        book,
                                        genre
                                )
                        )
                        .collect(Collectors.toSet());

        book.getGenres().addAll(
                bookGenres
        );

        /*
         * Save the updated book.
         */
        Book updatedBook =
                bookRepository.save(book);

        return bookMapper.toDetailResponse(
                updatedBook
        );
    }

    @Override
    @Transactional
    public void deleteBook(
            String id) {

        Book book = findBook(id);

        /*
         * First create the historical snapshot.
         *
         * The history contains the authors and genres
         * as names, preserving the state of the book
         * at deletion time.
         */
        bookHistoryRepository.save(
                bookHistoryMapper.toHistory(
                        book,
                        null
                )
        );

        /*
         * Because book_author and book_genre use
         * ON DELETE CASCADE, their records are removed
         * automatically by PostgreSQL.
         */
        bookRepository.delete(book);
    }

    private BookAuthor createBookAuthor(
            Book book,
            Author author) {

        BookAuthorId id =
                new BookAuthorId(
                        book.getId(),
                        author.getId()
                );

        return BookAuthor.builder()
                .id(id)
                .book(book)
                .author(author)
                .build();
    }

    private BookGenre createBookGenre(
            Book book,
            Genre genre) {

        BookGenreId id =
                new BookGenreId(
                        book.getId(),
                        genre.getId()
                );

        return BookGenre.builder()
                .id(id)
                .book(book)
                .genre(genre)
                .build();
    }

    private Book findBook(
            String id) {

        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with ID '"
                                        + id
                                        + "' not found"
                        )
                );
    }

    private void validateAuthors(
            List<Long> requestedIds,
            List<Author> foundAuthors) {

        if (requestedIds.size()
                != foundAuthors.size()) {

            Set<Long> foundIds =
                    foundAuthors.stream()
                            .map(Author::getId)
                            .collect(Collectors.toSet());

            List<Long> missingIds =
                    requestedIds.stream()
                            .filter(id ->
                                    !foundIds.contains(id)
                            )
                            .distinct()
                            .toList();

            throw new ResourceNotFoundException(
                    "Author(s) not found: "
                            + missingIds
            );
        }
    }

    private void validateGenres(
            List<Long> requestedIds,
            List<Genre> foundGenres) {

        if (requestedIds.size()
                != foundGenres.size()) {

            Set<Long> foundIds =
                    foundGenres.stream()
                            .map(Genre::getId)
                            .collect(Collectors.toSet());

            List<Long> missingIds =
                    requestedIds.stream()
                            .filter(id ->
                                    !foundIds.contains(id)
                            )
                            .distinct()
                            .toList();

            throw new ResourceNotFoundException(
                    "Genre(s) not found: "
                            + missingIds
            );
        }
    }

    private String normalizeTitle(
            String title) {

        if (title == null
                || title.isBlank()) {

            return "";
        }

        return title.trim();
    }

    private String normalizeIsbn(
            String isbn) {

        if (isbn == null
                || isbn.isBlank()) {

            return null;
        }

        return isbn.trim();
    }

    private List<Long> normalizeIds(
            List<Long> ids) {

        if (ids == null
                || ids.isEmpty()) {

            return null;
        }

        return ids;
    }
}