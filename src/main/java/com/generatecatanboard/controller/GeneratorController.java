package com.generatecatanboard.controller;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import com.generatecatanboard.service.GeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneratorController {

    private final GeneratorService generatorService;

    public GeneratorController(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @GetMapping(value = "/scenarioProps/{scenario}", produces = "application/json")
    public ScenarioProperties getScenarioProps(@PathVariable("scenario") String scenario) throws PropertiesNotFoundException {
        return generatorService.getScenarioProperties(scenario);
    }

    @GetMapping(value = "/randomBoard/{scenario}", produces = "application/json")
    public BoardData generateRandomBoard(@PathVariable("scenario") String scenario, @RequestParam String harbors) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        return generatorService.generateRandomBoard(scenario, harbors);
    }
}
