package com.library.entity;

import jakarta.persistence.*;
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
public class BookAuthorId implements Serializable {

    @Column(name = "book_id", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String bookId;

    @Column(name = "author_id")
    private Long authorId;
}