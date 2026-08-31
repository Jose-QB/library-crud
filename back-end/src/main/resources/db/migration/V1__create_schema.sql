```sql
-- =====================================================
-- LIBRARY CRUD - DATABASE SCHEMA
-- Migration: V1
-- =====================================================

CREATE SCHEMA IF NOT EXISTS library;

SET search_path TO library;

-- =====================================================
-- CATALOG TABLES
-- =====================================================

CREATE TABLE author (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    country VARCHAR(80),
    birth_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE genre (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE publisher (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    country VARCHAR(80),
    founded_year SMALLINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =====================================================
-- MAIN TABLE
-- =====================================================

CREATE TABLE book (
    id CHAR(3) PRIMARY KEY,

    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(17) UNIQUE,

    publication_year SMALLINT
        CHECK (publication_year BETWEEN 1400 AND 2100),

    edition VARCHAR(30),
    language VARCHAR(40),

    pages SMALLINT
        CHECK (pages > 0),

    stock INTEGER NOT NULL DEFAULT 0
        CHECK (stock >= 0),

    shelf_location VARCHAR(30),

    publisher_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_book_publisher
        FOREIGN KEY (publisher_id)
        REFERENCES library.publisher(id)
);

-- =====================================================
-- MANY TO MANY RELATIONS
-- =====================================================

CREATE TABLE book_author (
    book_id CHAR(3) NOT NULL,
    author_id BIGINT NOT NULL,

    PRIMARY KEY (book_id, author_id),

    CONSTRAINT fk_ba_book
        FOREIGN KEY (book_id)
        REFERENCES library.book(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ba_author
        FOREIGN KEY (author_id)
        REFERENCES library.author(id)
);

CREATE TABLE book_genre (
    book_id CHAR(3) NOT NULL,
    genre_id BIGINT NOT NULL,

    PRIMARY KEY (book_id, genre_id),

    CONSTRAINT fk_bg_book
        FOREIGN KEY (book_id)
        REFERENCES library.book(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bg_genre
        FOREIGN KEY (genre_id)
        REFERENCES library.genre(id)
);

-- =====================================================
-- HISTORY TABLE
-- =====================================================

CREATE TABLE book_history (
    history_id BIGSERIAL PRIMARY KEY,

    original_book_id CHAR(3) NOT NULL,

    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(17),

    publication_year SMALLINT,
    edition VARCHAR(30),
    language VARCHAR(40),

    pages SMALLINT,
    stock INTEGER,
    shelf_location VARCHAR(30),

    publisher_name VARCHAR(120),

    authors TEXT[] NOT NULL,
    genres TEXT[] NOT NULL,

    deleted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_by VARCHAR(60)
);

-- =====================================================
-- UPDATED_AT TRIGGER
-- =====================================================

CREATE OR REPLACE FUNCTION library.update_timestamp()
RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_book_updated_at
BEFORE UPDATE ON book
FOR EACH ROW
EXECUTE FUNCTION library.update_timestamp();

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_book_title
ON library.book(title);

CREATE INDEX idx_book_isbn
ON library.book(isbn);

CREATE INDEX idx_book_publisher
ON library.book(publisher_id);

CREATE INDEX idx_book_author_author
ON library.book_author(author_id);

CREATE INDEX idx_book_genre_genre
ON library.book_genre(genre_id);

CREATE INDEX idx_history_deleted_at
ON library.book_history(deleted_at DESC);

-- =====================================================
-- SAMPLE VIEW
-- =====================================================

CREATE OR REPLACE VIEW library.vw_book_detail AS
SELECT
    b.id,
    b.title,
    b.isbn,
    b.publication_year,
    b.edition,
    b.language,
    b.pages,
    b.stock,
    b.shelf_location,
    p.name AS publisher,
    STRING_AGG(DISTINCT a.name, ', ') AS authors,
    STRING_AGG(DISTINCT g.name, ', ') AS genres
FROM library.book b
JOIN library.publisher p
    ON p.id = b.publisher_id
LEFT JOIN library.book_author ba
    ON ba.book_id = b.id
LEFT JOIN library.author a
    ON a.id = ba.author_id
LEFT JOIN library.book_genre bg
    ON bg.book_id = b.id
LEFT JOIN library.genre g
    ON g.id = bg.genre_id
GROUP BY
    b.id,
    b.title,
    b.isbn,
    b.publication_year,
    b.edition,
    b.language,
    b.pages,
    b.stock,
    b.shelf_location,
    p.name;
```
