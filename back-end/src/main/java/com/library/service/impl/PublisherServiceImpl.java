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
import com.library.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public PublisherResponse createPublisher(
            CreatePublisherRequest request) {

        String name = request.getName().trim();

        if (publisherRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A publisher with name '" + name + "' already exists"
            );
        }

        Publisher publisher = publisherMapper.toEntity(request);
        publisher.setName(name);

        Publisher savedPublisher =
                publisherRepository.save(publisher);

        return publisherMapper.toResponse(savedPublisher);
    }

    @Override
    public List<PublisherResponse> getAllPublishers() {

        return publisherRepository.findAll()
                .stream()
                .map(publisherMapper::toResponse)
                .toList();
    }

    @Override
    public PublisherResponse getPublisherById(Long id) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Publisher with ID " + id + " not found"
                ));

        return publisherMapper.toResponse(publisher);
    }

    @Override
    @Transactional
    public PublisherResponse updatePublisher(
            Long id,
            UpdatePublisherRequest request) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Publisher with ID " + id + " not found"
                ));

        String name = request.getName().trim();

        if (!publisher.getName().equalsIgnoreCase(name)
                && publisherRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "A publisher with name '" + name + "' already exists"
            );
        }

        request.setName(name);

        publisherMapper.updateEntity(publisher, request);

        Publisher updatedPublisher =
                publisherRepository.save(publisher);

        return publisherMapper.toResponse(updatedPublisher);
    }

    @Override
    @Transactional
    public void deletePublisher(Long id) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Publisher with ID " + id + " not found"
                ));

        if (bookRepository.existsByPublisherId(id)) {
            throw new DuplicateResourceException(
                    "Publisher with ID " + id
                            + " cannot be deleted because it is used by one or more books"
            );
        }

        publisherRepository.delete(publisher);
    }
}