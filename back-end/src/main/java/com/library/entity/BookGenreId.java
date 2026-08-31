package com.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookGenreId implements Serializable {

    @Column(name = "book_id", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String bookId;

    @Column(name = "genre_id")
    private Long genreId;
}