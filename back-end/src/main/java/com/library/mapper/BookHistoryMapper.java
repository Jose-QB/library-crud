package com.library.mapper;

import com.library.entity.Book;
import com.library.entity.BookHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookHistoryMapper {

    public BookHistory toHistory(Book book, String deletedBy) {
        if (book == null) {
            return null;
        }

        List<String> authors = book.getAuthors()
                .stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<String> genres = book.getGenres()
                .stream()
                .map(bookGenre -> bookGenre.getGenre().getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        return BookHistory.builder()
                .originalBookId(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .edition(book.getEdition())
                .language(book.getLanguage())
                .pages(book.getPages())
                .stock(book.getStock())
                .shelfLocation(book.getShelfLocation())
                .publisherName(
                        book.getPublisher() != null
                                ? book.getPublisher().getName()
                                : null
                )
                .authors(authors)
                .genres(genres)
                .deletedBy(deletedBy)
                .build();
    }
}