package com.library.service;

import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.dto.response.BookResponse;

import java.util.List;

public interface BookService {

    BookDetailResponse createBook(CreateBookRequest request);

    List<BookResponse> searchBooks(
            String title,
            List<Long> authorIds,
            List<Long> genreIds
    );

    BookDetailResponse getBookById(String id);

    BookDetailResponse updateBook(
            String id,
            UpdateBookRequest request
    );

    void deleteBook(String id);
}