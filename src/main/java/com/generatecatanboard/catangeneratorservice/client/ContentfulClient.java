package com.generatecatanboard.catangeneratorservice.client;

import com.generatecatanboard.catangeneratorservice.client.domain.GetEntriesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "contentful-client", url = "${contentful.baseUrl}")
public interface ContentfulClient {
    // https://cdn.contentful.com/spaces/lemvlfz5icux/environments/master/entries?content_type=scenario

    @GetMapping(value = "spaces/${contentful.space}/environments/${contentful.environment}/entries?content_type={contentType}&access_token=${contentful.accessToken}")
    GetEntriesResponse getEntries(@PathVariable("contentType") String contentType);
}
