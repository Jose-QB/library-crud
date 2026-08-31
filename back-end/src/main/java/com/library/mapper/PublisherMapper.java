package com.library.mapper;

import com.library.dto.request.CreatePublisherRequest;
import com.library.dto.request.UpdatePublisherRequest;
import com.library.dto.response.PublisherResponse;
import com.library.entity.Publisher;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {

    public Publisher toEntity(CreatePublisherRequest request) {
        if (request == null) {
            return null;
        }

        return Publisher.builder()
                .name(request.getName())
                .country(request.getCountry())
                .foundedYear(request.getFoundedYear())
                .build();
    }

    public void updateEntity(
            Publisher publisher,
            UpdatePublisherRequest request) {

        publisher.setName(request.getName());
        publisher.setCountry(request.getCountry());
        publisher.setFoundedYear(request.getFoundedYear());
    }

    public PublisherResponse toResponse(Publisher publisher) {
        if (publisher == null) {
            return null;
        }

        return PublisherResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .country(publisher.getCountry())
                .foundedYear(publisher.getFoundedYear())
                .build();
    }
}