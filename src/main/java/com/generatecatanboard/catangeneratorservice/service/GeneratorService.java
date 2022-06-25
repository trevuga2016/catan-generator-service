package com.generatecatanboard.catangeneratorservice.service;

import com.generatecatanboard.catangeneratorservice.client.ContentfulClient;
import com.generatecatanboard.catangeneratorservice.client.domain.GetEntriesResponse;
import org.springframework.stereotype.Service;

@Service
public class GeneratorService {

    private final ContentfulClient contentfulClient;

    public GeneratorService(ContentfulClient contentfulClient) {
        this.contentfulClient = contentfulClient;
    }

    public GetEntriesResponse getContentType(String contentType) {
        return contentfulClient.getEntries(contentType);
    }
}
