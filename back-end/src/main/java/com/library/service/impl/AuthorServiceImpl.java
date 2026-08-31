package com.library.service.impl;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {

        String name = request.getName().trim();

        if (authorRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "An author with name '" + name + "' already exists"
            );
        }

        Author author = authorMapper.toEntity(request);
        author.setName(name);

        return authorMapper.toResponse(
                authorRepository.save(author)
        );
    }

    @Override
    public List<AuthorResponse> getAllAuthors() {

        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {

        Author author = findAuthor(id);

        return authorMapper.toResponse(author);
    }

    @Override
    @Transactional
    public AuthorResponse updateAuthor(
            Long id,
            UpdateAuthorRequest request) {

        Author author = findAuthor(id);

        String name = request.getName().trim();

        if (!author.getName().equalsIgnoreCase(name)
                && authorRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "An author with name '" + name + "' already exists"
            );
        }

        authorMapper.updateEntity(author, request);
        author.setName(name);

        return authorMapper.toResponse(
                authorRepository.save(author)
        );
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {

        findAuthor(id);

        if (bookRepository.existsByAuthorId(id)) {
            throw new DuplicateResourceException(
                    "Cannot delete author with ID " + id +
                            " because it is associated with one or more books"
            );
        }

        authorRepository.deleteById(id);
    }

    private Author findAuthor(Long id) {

        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Author with ID " + id + " not found"
                ));
    }
}