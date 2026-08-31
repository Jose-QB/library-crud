package com.library.service;

import com.library.dto.request.CreateGenreRequest;
import com.library.dto.request.UpdateGenreRequest;
import com.library.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {

    GenreResponse createGenre(CreateGenreRequest request);

    List<GenreResponse> getAllGenres();

    GenreResponse getGenreById(Long id);

    GenreResponse updateGenre(Long id, UpdateGenreRequest request);

    void deleteGenre(Long id);
}