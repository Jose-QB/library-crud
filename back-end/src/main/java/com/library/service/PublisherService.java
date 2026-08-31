package com.library.service;

import com.library.dto.request.CreatePublisherRequest;
import com.library.dto.request.UpdatePublisherRequest;
import com.library.dto.response.PublisherResponse;

import java.util.List;

public interface PublisherService {

    PublisherResponse createPublisher(CreatePublisherRequest request);

    List<PublisherResponse> getAllPublishers();

    PublisherResponse getPublisherById(Long id);

    PublisherResponse updatePublisher(
            Long id,
            UpdatePublisherRequest request
    );

    void deletePublisher(Long id);
}