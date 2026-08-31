```sql
-- =====================================================
-- LIBRARY CRUD - INITIAL DATA
-- Migration: V2
-- =====================================================

SET search_path TO library;

-- =====================================================
-- PUBLISHERS
-- =====================================================

INSERT INTO publisher (name, country, founded_year)
VALUES
    ('Penguin Random House', 'United States', 2013),
    ('Minotauro', 'Spain', 1955),
    ('Planeta', 'Spain', 1949);

-- =====================================================
-- AUTHORS
-- =====================================================

INSERT INTO author (name, country)
VALUES
    ('J. R. R. Tolkien', 'United Kingdom'),
    ('Christopher Tolkien', 'United Kingdom'),
    ('George Orwell', 'United Kingdom'),
    ('Gabriel García Márquez', 'Colombia');

-- =====================================================
-- GENRES
-- =====================================================

INSERT INTO genre (name, description)
VALUES
    ('Fantasy', 'Fantasy literature'),
    ('Adventure', 'Adventure stories'),
    ('Dystopian', 'Dystopian fiction'),
    ('Magical Realism', 'Magical realism novels');

-- =====================================================
-- BOOKS
-- =====================================================

INSERT INTO book
(
    id,
    title,
    isbn,
    publication_year,
    edition,
    language,
    pages,
    stock,
    shelf_location,
    publisher_id
)
VALUES
    (
        'A12',
        'The Silmarillion',
        '9788445071793',
        1977,
        '1st',
        'English',
        365,
        8,
        'A-01',
        2
    ),
    (
        'B34',
        '1984',
        '9780451524935',
        1949,
        '3rd',
        'English',
        328,
        12,
        'B-02',
        1
    ),
    (
        'C56',
        'One Hundred Years of Solitude',
        '9780307474728',
        1967,
        '2nd',
        'Spanish',
        471,
        5,
        'C-03',
        3
    );

-- =====================================================
-- BOOK / AUTHOR RELATIONS
-- =====================================================

INSERT INTO book_author (book_id, author_id)
VALUES
    ('A12', 1),
    ('A12', 2),
    ('B34', 3),
    ('C56', 4);

-- =====================================================
-- BOOK / GENRE RELATIONS
-- =====================================================

INSERT INTO book_genre (book_id, genre_id)
VALUES
    ('A12', 1),
    ('A12', 2),
    ('B34', 3),
    ('C56', 4);
```
