package com.generatecatanboard.service;

import com.generatecatanboard.client.ContentfulClient;
import com.generatecatanboard.client.domain.Fields;
import com.generatecatanboard.client.domain.GetEntriesResponse;
import com.generatecatanboard.client.domain.Items;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
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
