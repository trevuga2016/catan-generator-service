package com.generatecatanboard.catangeneratorservice.controller;

import com.generatecatanboard.catangeneratorservice.client.domain.GetEntriesResponse;
import com.generatecatanboard.catangeneratorservice.service.GeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneratorController {

    private final GeneratorService generatorService;

    public GeneratorController(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @GetMapping(value = "/scenario/{scenario}", produces = "application/json")
    public GetEntriesResponse test(@PathVariable("scenario") String scenario) {
        return generatorService.getContentType(scenario);
    }
}
