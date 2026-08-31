package com.library.controller;

import tools.jackson.databind.json.JsonMapper;
import com.library.dto.request.CreatePublisherRequest;
import com.library.dto.request.UpdatePublisherRequest;
import com.library.dto.response.PublisherResponse;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.PublisherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublisherController.class)
@Import(com.library.exception.GlobalExceptionHandler.class)
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private PublisherService publisherService;


    // ============================================================
    // GET /api/publishers
    // ============================================================

    @Test
    @DisplayName("GET /api/publishers - should return all publishers")
    void getPublishers_shouldReturnAllPublishers() throws Exception {

        List<PublisherResponse> publishers = List.of(
                PublisherResponse.builder()
                        .id(1L)
                        .name("Penguin Random House")
                        .country("United States")
                        .foundedYear((short) 2013)
                        .build(),

                PublisherResponse.builder()
                        .id(2L)
                        .name("Planeta")
                        .country("Spain")
                        .foundedYear((short) 1949)
                        .build()
        );

        when(publisherService.getAllPublishers())
                .thenReturn(publishers);

        mockMvc.perform(get("/api/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Penguin Random House"))
                .andExpect(jsonPath("$[0].country")
                        .value("United States"))
                .andExpect(jsonPath("$[0].foundedYear").value(2013))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name")
                        .value("Planeta"))
                .andExpect(jsonPath("$[1].country")
                        .value("Spain"))
                .andExpect(jsonPath("$[1].foundedYear").value(1949));

        verify(publisherService).getAllPublishers();
    }


    // ============================================================
    // GET /api/publishers/{id}
    // ============================================================

    @Test
    @DisplayName("GET /api/publishers/{id} - should return publisher")
    void getPublisher_shouldReturnPublisher() throws Exception {

        PublisherResponse response = PublisherResponse.builder()
                .id(1L)
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        when(publisherService.getPublisherById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/publishers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Penguin Random House"))
                .andExpect(jsonPath("$.country")
                        .value("United States"))
                .andExpect(jsonPath("$.foundedYear").value(2013));

        verify(publisherService).getPublisherById(1L);
    }


    @Test
    @DisplayName("GET /api/publishers/{id} - should return 404 when publisher does not exist")
    void getPublisher_shouldReturn404WhenPublisherDoesNotExist()
            throws Exception {

        when(publisherService.getPublisherById(999L))
                .thenThrow(
                        new ResourceNotFoundException("Publisher not found")
                );

        mockMvc.perform(get("/api/publishers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Publisher not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers/999"));

        verify(publisherService).getPublisherById(999L);
    }


    // ============================================================
    // POST /api/publishers
    // ============================================================

    @Test
    @DisplayName("POST /api/publishers - should create publisher")
    void createPublisher_shouldCreatePublisher() throws Exception {

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        PublisherResponse response = PublisherResponse.builder()
                .id(1L)
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        when(publisherService.createPublisher(
                any(CreatePublisherRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Penguin Random House"))
                .andExpect(jsonPath("$.country")
                        .value("United States"))
                .andExpect(jsonPath("$.foundedYear").value(2013));

        verify(publisherService)
                .createPublisher(any(CreatePublisherRequest.class));
    }


    @Test
    @DisplayName("POST /api/publishers - should return 400 when name is blank")
    void createPublisher_shouldReturn400WhenNameIsBlank()
            throws Exception {

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("")
                .country("Mexico")
                .foundedYear((short) 2000)
                .build();

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers"))
                .andExpect(jsonPath("$.details[0]")
                        .value("name: Name is required"));
    }


    @Test
    @DisplayName("POST /api/publishers - should return 400 when name exceeds 120 characters")
    void createPublisher_shouldReturn400WhenNameExceedsMaxLength()
            throws Exception {

        String name = "A".repeat(121);

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name(name)
                .country("Mexico")
                .foundedYear((short) 2000)
                .build();

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("POST /api/publishers - should return 400 when country exceeds 80 characters")
    void createPublisher_shouldReturn400WhenCountryExceedsMaxLength()
            throws Exception {

        String country = "A".repeat(81);

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("Publisher")
                .country(country)
                .foundedYear((short) 2000)
                .build();

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("POST /api/publishers - should return 400 when founded year is below 1400")
    void createPublisher_shouldReturn400WhenFoundedYearIsTooLow()
            throws Exception {

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("Publisher")
                .country("Mexico")
                .foundedYear((short) 1399)
                .build();

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("POST /api/publishers - should return 400 when founded year exceeds 2100")
    void createPublisher_shouldReturn400WhenFoundedYearIsTooHigh()
            throws Exception {

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("Publisher")
                .country("Mexico")
                .foundedYear((short) 2101)
                .build();

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("POST /api/publishers - should return 409 when publisher already exists")
    void createPublisher_shouldReturn409WhenPublisherAlreadyExists()
            throws Exception {

        CreatePublisherRequest request = CreatePublisherRequest.builder()
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        when(publisherService.createPublisher(
                any(CreatePublisherRequest.class)
        )).thenThrow(
                new DuplicateResourceException(
                        "Publisher already exists"
                )
        );

        mockMvc.perform(
                        post("/api/publishers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Publisher already exists"))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers"));

        verify(publisherService)
                .createPublisher(any(CreatePublisherRequest.class));
    }


    // ============================================================
    // PUT /api/publishers/{id}
    // ============================================================

    @Test
    @DisplayName("PUT /api/publishers/{id} - should update publisher")
    void updatePublisher_shouldUpdatePublisher() throws Exception {

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        PublisherResponse response = PublisherResponse.builder()
                .id(1L)
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        when(publisherService.updatePublisher(
                eq(1L),
                any(UpdatePublisherRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/publishers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Penguin Random House"))
                .andExpect(jsonPath("$.country")
                        .value("United States"))
                .andExpect(jsonPath("$.foundedYear").value(2013));

        verify(publisherService)
                .updatePublisher(
                        eq(1L),
                        any(UpdatePublisherRequest.class)
                );
    }


    @Test
    @DisplayName("PUT /api/publishers/{id} - should return 404 when publisher does not exist")
    void updatePublisher_shouldReturn404WhenPublisherDoesNotExist()
            throws Exception {

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("Updated Publisher")
                .country("Mexico")
                .foundedYear((short) 2000)
                .build();

        when(publisherService.updatePublisher(
                eq(999L),
                any(UpdatePublisherRequest.class)
        )).thenThrow(
                new ResourceNotFoundException("Publisher not found")
        );

        mockMvc.perform(
                        put("/api/publishers/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Publisher not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers/999"));

        verify(publisherService)
                .updatePublisher(
                        eq(999L),
                        any(UpdatePublisherRequest.class)
                );
    }


    @Test
    @DisplayName("PUT /api/publishers/{id} - should return 400 when name is blank")
    void updatePublisher_shouldReturn400WhenNameIsBlank()
            throws Exception {

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("")
                .country("Mexico")
                .foundedYear((short) 2000)
                .build();

        mockMvc.perform(
                        put("/api/publishers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.details[0]")
                        .value("name: Name is required"));
    }


    @Test
    @DisplayName("PUT /api/publishers/{id} - should return 400 when founded year is below 1400")
    void updatePublisher_shouldReturn400WhenFoundedYearIsTooLow()
            throws Exception {

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("Publisher")
                .country("Mexico")
                .foundedYear((short) 1399)
                .build();

        mockMvc.perform(
                        put("/api/publishers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("PUT /api/publishers/{id} - should return 400 when founded year exceeds 2100")
    void updatePublisher_shouldReturn400WhenFoundedYearIsTooHigh()
            throws Exception {

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("Publisher")
                .country("Mexico")
                .foundedYear((short) 2101)
                .build();

        mockMvc.perform(
                        put("/api/publishers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }


    @Test
    @DisplayName("PUT /api/publishers/{id} - should return 409 when publisher name already exists")
    void updatePublisher_shouldReturn409WhenNameAlreadyExists()
            throws Exception {

        UpdatePublisherRequest request = UpdatePublisherRequest.builder()
                .name("Existing Publisher")
                .country("Mexico")
                .foundedYear((short) 2000)
                .build();

        when(publisherService.updatePublisher(
                eq(1L),
                any(UpdatePublisherRequest.class)
        )).thenThrow(
                new DuplicateResourceException(
                        "Publisher with the same name already exists"
                )
        );

        mockMvc.perform(
                        put("/api/publishers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Publisher with the same name already exists"
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers/1"));

        verify(publisherService)
                .updatePublisher(
                        eq(1L),
                        any(UpdatePublisherRequest.class)
                );
    }


    // ============================================================
    // DELETE /api/publishers/{id}
    // ============================================================

    @Test
    @DisplayName("DELETE /api/publishers/{id} - should delete publisher")
    void deletePublisher_shouldDeletePublisher() throws Exception {

        doNothing()
                .when(publisherService)
                .deletePublisher(1L);

        mockMvc.perform(delete("/api/publishers/1"))
                .andExpect(status().isNoContent());

        verify(publisherService).deletePublisher(1L);
    }


    @Test
    @DisplayName("DELETE /api/publishers/{id} - should return 404 when publisher does not exist")
    void deletePublisher_shouldReturn404WhenPublisherDoesNotExist()
            throws Exception {

        doThrow(
                new ResourceNotFoundException("Publisher not found")
        )
                .when(publisherService)
                .deletePublisher(999L);

        mockMvc.perform(delete("/api/publishers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Publisher not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers/999"));

        verify(publisherService).deletePublisher(999L);
    }


    @Test
    @DisplayName("DELETE /api/publishers/{id} - should return 409 when publisher is associated with books")
    void deletePublisher_shouldReturn409WhenPublisherHasBooks()
            throws Exception {

        doThrow(
                new DuplicateResourceException(
                        "Publisher cannot be deleted because it is associated with one or more books"
                )
        )
                .when(publisherService)
                .deletePublisher(1L);

        mockMvc.perform(delete("/api/publishers/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Publisher cannot be deleted because it is associated with one or more books"
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/publishers/1"));

        verify(publisherService).deletePublisher(1L);
    }
}
