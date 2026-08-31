-- =====================================================
-- LIBRARY CRUD - PostgreSQL
-- =====================================================

DROP SCHEMA IF EXISTS library CASCADE;
CREATE SCHEMA library;

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
    id CHAR(3) PRIMARY KEY,                       -- Ej. A12

    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(17) UNIQUE,

    publication_year SMALLINT
        CHECK (publication_year BETWEEN 1400 AND 2100),

    edition VARCHAR(30),
    language VARCHAR(40),

    pages SMALLINT CHECK (pages > 0),

    stock INTEGER NOT NULL DEFAULT 0
        CHECK (stock >= 0),

    shelf_location VARCHAR(30),

    publisher_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_book_publisher
        FOREIGN KEY (publisher_id)
        REFERENCES publisher(id)
);

-- =====================================================
-- MANY TO MANY RELATIONS
-- =====================================================

CREATE TABLE book_author (
    book_id CHAR(3) NOT NULL,
    author_id BIGINT NOT NULL,

    PRIMARY KEY(book_id, author_id),

    CONSTRAINT fk_ba_book
        FOREIGN KEY(book_id)
        REFERENCES book(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ba_author
        FOREIGN KEY(author_id)
        REFERENCES author(id)
);

CREATE TABLE book_genre (
    book_id CHAR(3) NOT NULL,
    genre_id BIGINT NOT NULL,

    PRIMARY KEY(book_id, genre_id),

    CONSTRAINT fk_bg_book
        FOREIGN KEY(book_id)
        REFERENCES book(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bg_genre
        FOREIGN KEY(genre_id)
        REFERENCES genre(id)
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

CREATE OR REPLACE FUNCTION update_timestamp()
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
EXECUTE FUNCTION update_timestamp();

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_book_title
ON book(title);

CREATE INDEX idx_book_isbn
ON book(isbn);

CREATE INDEX idx_book_publisher
ON book(publisher_id);

CREATE INDEX idx_book_author_author
ON book_author(author_id);

CREATE INDEX idx_book_genre_genre
ON book_genre(genre_id);

CREATE INDEX idx_history_deleted_at
ON book_history(deleted_at DESC);

-- =====================================================
-- SEED DATA
-- =====================================================

INSERT INTO publisher (name, country, founded_year)
VALUES
('Penguin Random House', 'United States', 2013),
('Minotauro', 'Spain', 1955),
('Planeta', 'Spain', 1949);

INSERT INTO author (name, country)
VALUES
('J. R. R. Tolkien', 'United Kingdom'),
('Christopher Tolkien', 'United Kingdom'),
('George Orwell', 'United Kingdom'),
('Gabriel García Márquez', 'Colombia');

INSERT INTO genre (name, description)
VALUES
('Fantasy', 'Fantasy literature'),
('Adventure', 'Adventure stories'),
('Dystopian', 'Dystopian fiction'),
('Magical Realism', 'Magical realism novels');

INSERT INTO book
(id, title, isbn, publication_year, edition, language, pages, stock, shelf_location, publisher_id)
VALUES
('A12', 'The Silmarillion', '9788445071793', 1977, '1st', 'English', 365, 8, 'A-01', 2),
('B34', '1984', '9780451524935', 1949, '3rd', 'English', 328, 12, 'B-02', 1),
('C56', 'One Hundred Years of Solitude', '9780307474728', 1967, '2nd', 'Spanish', 471, 5, 'C-03', 3);

INSERT INTO book_author VALUES
('A12', 1),
('A12', 2),
('B34', 3),
('C56', 4);

INSERT INTO book_genre VALUES
('A12', 1),
('A12', 2),
('B34', 3),
('C56', 4);

-- =====================================================
-- SAMPLE VIEW
-- =====================================================

CREATE OR REPLACE VIEW vw_book_detail AS
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
FROM book b
JOIN publisher p ON p.id = b.publisher_id
LEFT JOIN book_author ba ON ba.book_id = b.id
LEFT JOIN author a ON a.id = ba.author_id
LEFT JOIN book_genre bg ON bg.book_id = b.id
LEFT JOIN genre g ON g.id = bg.genre_id
GROUP BY
    b.id, b.title, b.isbn, b.publication_year,
    b.edition, b.language, b.pages,
    b.stock, b.shelf_location, p.name;

-- =====================================================
-- END
-- =====================================================