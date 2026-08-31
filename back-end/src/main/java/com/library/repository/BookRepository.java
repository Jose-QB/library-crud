package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    @Query("""
        SELECT DISTINCT b
        FROM Book b
        LEFT JOIN b.authors ba
        LEFT JOIN ba.author a
        LEFT JOIN b.genres bg
        LEFT JOIN bg.genre g
        WHERE
            (:title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND
            (:authorIds IS NULL OR a.id IN :authorIds)
        AND
            (:genreIds IS NULL OR g.id IN :genreIds)
        """)
    List<Book> searchBooks(
            @Param("title") String title,
            @Param("authorIds") List<Long> authorIds,
            @Param("genreIds") List<Long> genreIds
    );

    boolean existsByIsbn(String isbn);

    @Query("""
        SELECT COUNT(b) > 0
        FROM Book b
        JOIN b.authors ba
        WHERE ba.author.id = :authorId
        """)
    boolean existsByAuthorId(@Param("authorId") Long authorId);

    @Query("""
        SELECT COUNT(b) > 0
        FROM Book b
        JOIN b.genres bg
        WHERE bg.genre.id = :genreId
        """)
    boolean existsByGenreId(@Param("genreId") Long genreId);

    @Query("""
        SELECT COUNT(b) > 0
        FROM Book b
        WHERE b.publisher.id = :publisherId
        """)
    boolean existsByPublisherId(@Param("publisherId") Long publisherId);

    @Query("SELECT b.id FROM Book b")
    List<String> findAllIds();
}