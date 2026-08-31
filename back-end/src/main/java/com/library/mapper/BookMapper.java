package com.library.mapper;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.AuthorResponse;
import com.library.dto.response.BookDetailResponse;
import com.library.dto.response.BookResponse;
import com.library.dto.response.GenreResponse;
import com.library.dto.response.PublisherResponse;
import com.library.entity.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {

    public Book toEntity(CreateBookRequest request) {
        if (request == null) {
            return null;
        }

        return Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publicationYear(request.getPublicationYear())
                .edition(request.getEdition())
                .language(request.getLanguage())
                .pages(request.getPages())
                .stock(request.getStock())
                .shelfLocation(request.getShelfLocation())
                .build();
    }

    public void updateEntity(Book book, UpdateBookRequest request) {
        if (book == null || request == null) {
            return;
        }

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setEdition(request.getEdition());
        book.setLanguage(request.getLanguage());
        book.setPages(request.getPages());
        book.setStock(request.getStock());
        book.setShelfLocation(request.getShelfLocation());
    }

    public BookResponse toResponse(Book book) {
        if (book == null) {
            return null;
        }

        List<String> authors = book.getAuthors()
                .stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .sorted()
                .toList();

        List<String> genres = book.getGenres()
                .stream()
                .map(bookGenre -> bookGenre.getGenre().getName())
                .sorted()
                .toList();

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .publisher(
                        book.getPublisher() != null
                                ? book.getPublisher().getName()
                                : null
                )
                .authors(authors)
                .genres(genres)
                .stock(book.getStock())
                .shelfLocation(book.getShelfLocation())
                .build();
    }

    public BookDetailResponse toDetailResponse(Book book) {
        if (book == null) {
            return null;
        }

        List<AuthorResponse> authors = book.getAuthors()
                .stream()
                .map(bookAuthor -> bookAuthor.getAuthor())
                .map(this::toAuthorResponse)
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();

        List<GenreResponse> genres = book.getGenres()
                .stream()
                .map(bookGenre -> bookGenre.getGenre())
                .map(this::toGenreResponse)
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();

        PublisherResponse publisher = book.getPublisher() != null
                ? toPublisherResponse(book.getPublisher())
                : null;

        return BookDetailResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .edition(book.getEdition())
                .language(book.getLanguage())
                .pages(book.getPages())
                .stock(book.getStock())
                .shelfLocation(book.getShelfLocation())
                .publisher(publisher)
                .authors(authors)
                .genres(genres)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    private AuthorResponse toAuthorResponse(
            com.library.entity.Author author) {

        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .country(author.getCountry())
                .birthDate(author.getBirthDate())
                .build();
    }

    private GenreResponse toGenreResponse(
            com.library.entity.Genre genre) {

        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }

    private PublisherResponse toPublisherResponse(
            com.library.entity.Publisher publisher) {

        return PublisherResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .country(publisher.getCountry())
                .foundedYear(publisher.getFoundedYear())
                .build();
    }
}