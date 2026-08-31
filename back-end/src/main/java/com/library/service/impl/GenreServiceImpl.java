package com.library.service.impl;

import com.library.dto.request.CreateGenreRequest;
import com.library.dto.request.UpdateGenreRequest;
import com.library.dto.response.GenreResponse;
import com.library.entity.Genre;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.GenreMapper;
import com.library.repository.BookRepository;
import com.library.repository.GenreRepository;
import com.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public GenreResponse createGenre(CreateGenreRequest request) {

        String name = request.getName().trim();

        if (genreRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A genre with name '" + name + "' already exists"
            );
        }

        Genre genre = genreMapper.toEntity(request);
        genre.setName(name);

        Genre savedGenre = genreRepository.save(genre);

        return genreMapper.toResponse(savedGenre);
    }

    @Override
    public List<GenreResponse> getAllGenres() {

        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toResponse)
                .toList();
    }

    @Override
    public GenreResponse getGenreById(Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with ID " + id + " not found"
                ));

        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional
    public GenreResponse updateGenre(
            Long id,
            UpdateGenreRequest request) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with ID " + id + " not found"
                ));

        String name = request.getName().trim();

        if (!genre.getName().equalsIgnoreCase(name)
                && genreRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "A genre with name '" + name + "' already exists"
            );
        }

        request.setName(name);

        genreMapper.updateEntity(genre, request);

        Genre updatedGenre = genreRepository.save(genre);

        return genreMapper.toResponse(updatedGenre);
    }

    @Override
    @Transactional
    public void deleteGenre(Long id) {

        genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with ID " + id + " not found"
                ));

        if (bookRepository.existsByGenreId(id)) {
            throw new DuplicateResourceException(
                    "Cannot delete genre with ID " + id +
                            " because it is associated with one or more books"
            );
        }

        genreRepository.deleteById(id);
    }
}