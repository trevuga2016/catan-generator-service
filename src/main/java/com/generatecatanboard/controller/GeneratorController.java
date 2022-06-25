package com.generatecatanboard.controller;

import com.generatecatanboard.client.domain.Fields;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import com.generatecatanboard.service.GeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneratorController {

    private final GeneratorService generatorService;

    public GeneratorController(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @GetMapping(value = "/scenarioProps/{scenario}", produces = "application/json")
    public Fields getScenarioProps(@PathVariable("scenario") String scenario) throws PropertiesNotFoundException {
        return generatorService.getScenarioProperties(scenario);
    }
}
