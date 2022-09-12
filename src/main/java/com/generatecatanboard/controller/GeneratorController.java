package com.generatecatanboard.controller;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.BuildingCosts;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import com.generatecatanboard.service.GeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeneratorController {

    private final GeneratorService generatorService;

    public GeneratorController(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @Operation(summary = "Get scenario properties", description = "Get Scenario Properties")
    @GetMapping(value = "/scenarioProps/{scenario}", produces = "application/json")
    public ScenarioProperties getScenarioProps(@PathVariable("scenario") String scenario) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        return generatorService.getScenarioProperties(scenario);
    }

    @Operation(summary = "Generate a random game board", description = "Generate Random Board")
    @GetMapping(value = "/randomBoard/{scenario}", produces = "application/json")
    public BoardData generateRandomBoard(@PathVariable("scenario") String scenario, @RequestParam String harbors) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        return generatorService.generateRandomBoard(scenario, harbors);
    }

    @Operation(summary = "Get the building costs for a scenario", description = "Get Building Costs")
    @GetMapping(value = "/buildingCosts/{scenario}", produces = "application/json")
    public List<BuildingCosts> getBuildingCosts(@PathVariable("scenario") String scenario) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        return generatorService.getBuildingCosts(scenario);
    }
}
