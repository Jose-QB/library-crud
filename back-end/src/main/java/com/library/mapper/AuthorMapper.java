package com.library.mapper;

import com.library.dto.request.CreateAuthorRequest;
import com.library.dto.request.UpdateAuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(CreateAuthorRequest request) {
        if (request == null) return null;

        return Author.builder()
                .name(request.getName())
                .country(request.getCountry())
                .birthDate(request.getBirthDate())
                .build();
    }

    public void updateEntity(Author author, UpdateAuthorRequest request) {
        author.setName(request.getName());
        author.setCountry(request.getCountry());
        author.setBirthDate(request.getBirthDate());
    }

    public AuthorResponse toResponse(Author author) {
        if (author == null) return null;

        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .country(author.getCountry())
                .birthDate(author.getBirthDate())
                .build();
    }
}