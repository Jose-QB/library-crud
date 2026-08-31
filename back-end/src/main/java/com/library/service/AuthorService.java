package com.library.service;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;

import java.util.List;

public interface AuthorService {

    AuthorResponse createAuthor(CreateAuthorRequest request);

    List<AuthorResponse> getAllAuthors();

    AuthorResponse getAuthorById(Long id);

    AuthorResponse updateAuthor(Long id, UpdateAuthorRequest request);

    void deleteAuthor(Long id);
}