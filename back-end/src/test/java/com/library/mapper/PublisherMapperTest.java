package com.library.mapper;

import com.library.dto.request.CreatePublisherRequest;
import com.library.dto.request.UpdatePublisherRequest;
import com.library.dto.response.PublisherResponse;
import com.library.entity.Publisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublisherMapperTest {

    private final PublisherMapper mapper = new PublisherMapper();

    @Test
    @DisplayName("toEntity - should map create request to entity")
    void toEntity_shouldMapRequestToEntity() {

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        Publisher result = mapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Penguin Random House", result.getName());
        assertEquals("United States", result.getCountry());
        assertEquals(
                Short.valueOf((short) 2013),
                result.getFoundedYear()
        );
    }

    @Test
    @DisplayName("toEntity - should return null when request is null")
    void toEntity_shouldReturnNullWhenRequestIsNull() {

        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("updateEntity - should update entity with request values")
    void updateEntity_shouldUpdateEntity() {

        Publisher publisher = Publisher.builder()
                .id(1L)
                .name("Old Publisher")
                .country("Mexico")
                .foundedYear((short) 1950)
                .build();

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        mapper.updateEntity(publisher, request);

        assertEquals("Penguin Random House", publisher.getName());
        assertEquals("United States", publisher.getCountry());
        assertEquals(
                Short.valueOf((short) 2013),
                publisher.getFoundedYear()
        );
        assertEquals(1L, publisher.getId());
    }

    @Test
    @DisplayName("toResponse - should map entity to response")
    void toResponse_shouldMapEntityToResponse() {

        Publisher publisher = Publisher.builder()
                .id(1L)
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        PublisherResponse result = mapper.toResponse(publisher);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Penguin Random House", result.getName());
        assertEquals("United States", result.getCountry());
        assertEquals(
                Short.valueOf((short) 2013),
                result.getFoundedYear()
        );
    }

    @Test
    @DisplayName("toResponse - should return null when entity is null")
    void toResponse_shouldReturnNullWhenEntityIsNull() {

        assertNull(mapper.toResponse(null));
    }
}