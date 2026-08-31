package com.library.service.impl;

import com.library.dto.request.CreatePublisherRequest;
import com.library.dto.request.UpdatePublisherRequest;
import com.library.dto.response.PublisherResponse;
import com.library.entity.Publisher;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.PublisherMapper;
import com.library.repository.BookRepository;
import com.library.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private PublisherMapper publisherMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    private Publisher publisher;
    private PublisherResponse publisherResponse;

    @BeforeEach
    void setUp() {

        publisher = Publisher.builder()
                .id(1L)
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();

        publisherResponse = PublisherResponse.builder()
                .id(1L)
                .name("Penguin Random House")
                .country("United States")
                .foundedYear((short) 2013)
                .build();
    }

    @Test
    void shouldCreatePublisher() {

        CreatePublisherRequest request =
                CreatePublisherRequest.builder()
                        .name("Penguin Random House")
                        .country("United States")
                        .foundedYear((short) 2013)
                        .build();

        when(publisherRepository.existsByNameIgnoreCase(
                "Penguin Random House"))
                .thenReturn(false);

        when(publisherMapper.toEntity(request))
                .thenReturn(publisher);

        when(publisherRepository.save(publisher))
                .thenReturn(publisher);

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        PublisherResponse result =
                publisherService.createPublisher(request);

        assertThat(result).isEqualTo(publisherResponse);

        verify(publisherRepository)
                .existsByNameIgnoreCase(
                        "Penguin Random House");

        verify(publisherMapper).toEntity(request);
        verify(publisherRepository).save(publisher);
        verify(publisherMapper).toResponse(publisher);
    }

    @Test
    void shouldTrimPublisherNameWhenCreating() {

        CreatePublisherRequest request =
                CreatePublisherRequest.builder()
                        .name("  Penguin Random House  ")
                        .build();

        when(publisherRepository.existsByNameIgnoreCase(
                "Penguin Random House"))
                .thenReturn(false);

        when(publisherMapper.toEntity(request))
                .thenReturn(publisher);

        when(publisherRepository.save(publisher))
                .thenReturn(publisher);

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        publisherService.createPublisher(request);

        assertThat(publisher.getName())
                .isEqualTo("Penguin Random House");
    }

    @Test
    void shouldRejectDuplicatePublisher() {

        CreatePublisherRequest request =
                CreatePublisherRequest.builder()
                        .name("Penguin Random House")
                        .build();

        when(publisherRepository.existsByNameIgnoreCase(
                "Penguin Random House"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                publisherService.createPublisher(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "A publisher with name 'Penguin Random House' already exists"
                );

        verify(publisherRepository, never())
                .save(any());

        verify(publisherMapper, never())
                .toEntity(any());
    }

    @Test
    void shouldGetAllPublishers() {

        Publisher secondPublisher =
                Publisher.builder()
                        .id(2L)
                        .name("Planeta")
                        .build();

        PublisherResponse secondResponse =
                PublisherResponse.builder()
                        .id(2L)
                        .name("Planeta")
                        .build();

        when(publisherRepository.findAll())
                .thenReturn(List.of(
                        publisher,
                        secondPublisher
                ));

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        when(publisherMapper.toResponse(secondPublisher))
                .thenReturn(secondResponse);

        List<PublisherResponse> result =
                publisherService.getAllPublishers();

        assertThat(result)
                .containsExactly(
                        publisherResponse,
                        secondResponse
                );

        verify(publisherRepository).findAll();
    }

    @Test
    void shouldGetPublisherById() {

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        PublisherResponse result =
                publisherService.getPublisherById(1L);

        assertThat(result).isEqualTo(publisherResponse);

        verify(publisherRepository).findById(1L);
        verify(publisherMapper).toResponse(publisher);
    }

    @Test
    void shouldThrowExceptionWhenPublisherDoesNotExist() {

        when(publisherRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                publisherService.getPublisherById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Publisher with ID 99 not found"
                );

        verifyNoInteractions(publisherMapper);
    }

    @Test
    void shouldUpdatePublisher() {

        UpdatePublisherRequest request =
                UpdatePublisherRequest.builder()
                        .name("Minotauro")
                        .country("Spain")
                        .foundedYear((short) 1955)
                        .build();

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(publisherRepository.existsByNameIgnoreCase(
                "Minotauro"))
                .thenReturn(false);

        when(publisherRepository.save(publisher))
                .thenReturn(publisher);

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        PublisherResponse result =
                publisherService.updatePublisher(1L, request);

        assertThat(result).isEqualTo(publisherResponse);

        verify(publisherMapper)
                .updateEntity(publisher, request);

        verify(publisherRepository).save(publisher);
    }

    @Test
    void shouldTrimPublisherNameWhenUpdating() {

        UpdatePublisherRequest request =
                UpdatePublisherRequest.builder()
                        .name("  Minotauro  ")
                        .build();

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(publisherRepository.save(publisher))
                .thenReturn(publisher);

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        publisherService.updatePublisher(1L, request);

        assertThat(request.getName())
                .isEqualTo("Minotauro");
    }

    @Test
    void shouldRejectDuplicatePublisherWhenUpdating() {

        UpdatePublisherRequest request =
                UpdatePublisherRequest.builder()
                        .name("Planeta")
                        .build();

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(publisherRepository.existsByNameIgnoreCase(
                "Planeta"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                publisherService.updatePublisher(1L, request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(publisherRepository, never())
                .save(any());

        verify(publisherMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void shouldNotCheckDuplicateWhenOnlyCaseChanges() {

        publisher.setName("Planeta");

        UpdatePublisherRequest request =
                UpdatePublisherRequest.builder()
                        .name("planeta")
                        .build();

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(publisherRepository.save(publisher))
                .thenReturn(publisher);

        when(publisherMapper.toResponse(publisher))
                .thenReturn(publisherResponse);

        publisherService.updatePublisher(1L, request);

        verify(publisherRepository, never())
                .existsByNameIgnoreCase(anyString());

        verify(publisherRepository).save(publisher);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPublisher() {

        UpdatePublisherRequest request =
                UpdatePublisherRequest.builder()
                        .name("Planeta")
                        .build();

        when(publisherRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                publisherService.updatePublisher(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(publisherRepository, never())
                .save(any());
    }

    @Test
    void shouldDeletePublisher() {

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(bookRepository.existsByPublisherId(1L))
                .thenReturn(false);

        publisherService.deletePublisher(1L);

        verify(publisherRepository).findById(1L);
        verify(bookRepository).existsByPublisherId(1L);
        verify(publisherRepository).delete(publisher);
    }

    @Test
    void shouldNotDeletePublisherAssociatedWithBooks() {

        when(publisherRepository.findById(1L))
                .thenReturn(Optional.of(publisher));

        when(bookRepository.existsByPublisherId(1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                publisherService.deletePublisher(1L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "Publisher with ID 1 cannot be deleted"
                );

        verify(publisherRepository, never())
                .delete(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingPublisher() {

        when(publisherRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                publisherService.deletePublisher(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, never())
                .existsByPublisherId(anyLong());

        verify(publisherRepository, never())
                .delete(any());
    }
}