package com.generatecatanboard.catangeneratorservice.service;

import com.generatecatanboard.catangeneratorservice.client.ContentfulClient;
import com.generatecatanboard.catangeneratorservice.client.domain.Fields;
import com.generatecatanboard.catangeneratorservice.client.domain.GetEntriesResponse;
import com.generatecatanboard.catangeneratorservice.client.domain.Items;
import com.generatecatanboard.catangeneratorservice.exceptions.PropertiesNotFoundException;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratorService {

    private final ContentfulClient contentfulClient;

    public GeneratorService(ContentfulClient contentfulClient) {
        this.contentfulClient = contentfulClient;
    }

    public Fields getScenarioProperties(String scenario) throws PropertiesNotFoundException {
        try {
            GetEntriesResponse getEntriesResponse = contentfulClient.getEntries("scenario");
            List<Items> items = getEntriesResponse.getItems();
            Items foundItem = items.stream()
                    .filter(item -> scenario.equals(item.getFields().getScenarioUrl()))
                    .findAny()
                    .orElse(null);
            if (foundItem != null) {
                return foundItem.getFields();
            } else {
                throw new PropertiesNotFoundException("No scenario properties were returned from scenario '".concat(scenario).concat("'"));
            }
        } catch (FeignException.FeignClientException fe) {
            throw new PropertiesNotFoundException("A ".concat(String.valueOf(fe.status())).concat(" exception occurred while calling Contentful"), fe.getCause());
        }
    }
}
