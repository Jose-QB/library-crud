package com.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "book_history",
        schema = "library"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "original_book_id", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String originalBookId;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Column(length = 17)
    private String isbn;

    @Column(name = "publication_year")
    private Short publicationYear;

    @Column(length = 30)
    private String edition;

    @Column(length = 40)
    private String language;

    @Column
    private Short pages;

    @Column
    private Integer stock;

    @Column(name = "shelf_location", length = 30)
    private String shelfLocation;

    @Column(name = "publisher_name", length = 120)
    private String publisherName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(
            name = "authors",
            nullable = false,
            columnDefinition = "TEXT[]"
    )
    private List<String> authors;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(
            name = "genres",
            nullable = false,
            columnDefinition = "TEXT[]"
    )
    private List<String> genres;

    @CreationTimestamp
    @Column(name = "deleted_at", nullable = false, updatable = false)
    private LocalDateTime deletedAt;

    @Column(
            name = "deleted_by",
            length = 60
    )
    private String deletedBy;
}