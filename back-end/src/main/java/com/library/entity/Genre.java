package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "genre",
        schema = "library"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}