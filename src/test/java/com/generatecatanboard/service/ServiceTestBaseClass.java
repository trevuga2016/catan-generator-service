package com.generatecatanboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.BuildingCosts;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.GameResourcesConfig;
import com.generatecatanboard.domain.ScenarioProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServiceTestBaseClass {

    public GameHarborConfig getMockGameHarborConfig() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockGameHarborConfig.json"), GameHarborConfig.class);
    }

    public GameResourcesConfig getMockGameResourcesConfig() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockGameResourceConfig.json"), GameResourcesConfig.class);
    }

    public ScenarioProperties getMockScenarioProps() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockScenarioProps.json"), ScenarioProperties.class);
    }

    public ScenarioProperties getMockCitiesAndKnightsProps() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockScenarioProps_ck.json"), ScenarioProperties.class);
    }

    public BoardData getMockBoardData() throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(this.getClass().getClassLoader().getResourceAsStream("mocks/mockBoardData.json"), BoardData.class);
    }

    public BuildingCosts getMockBuildingCost1() throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(this.getClass().getClassLoader().getResourceAsStream("mocks/mockBuildingCost1.json"), BuildingCosts.class);
    }

    public BuildingCosts getMockBuildingCost2() throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(this.getClass().getClassLoader().getResourceAsStream("mocks/mockBuildingCost2.json"), BuildingCosts.class);
    }

    public List<String> getMockListOfResources() {
        return new LinkedList<>(Arrays.asList("Ore", "Ore", "Ore", "Wool", "Wool", "Wool", "Wool", "Brick", "Brick", "Brick", "Grain",
                "Grain", "Grain", "Grain", "Lumber", "Lumber", "Lumber", "Lumber", "Desert"));
    }

    public List<Double> getMockRowConfig() {
        return new LinkedList<>(Arrays.asList(3.0, 4.0, 5.0, 4.0, 3.0));
    }

    public List<String> getMockListOfNumbers() {
        return new LinkedList<>(Arrays.asList("2", "3", "3", "4", "4", "5", "5", "6", "6", "8", "8", "9", "9", "10", "10", "11", "11", "12"));
    }

    public Map<String, Double> mockNumbersFrequency() {
        Map<String, Double> numbersFrequency = new HashMap<>();
        numbersFrequency.put("2", 1.0);
        numbersFrequency.put("3", 2.0);
        numbersFrequency.put("4", 2.0);
        numbersFrequency.put("5", 2.0);
        numbersFrequency.put("6", 2.0);
        numbersFrequency.put("8", 2.0);
        numbersFrequency.put("9", 2.0);
        numbersFrequency.put("10", 2.0);
        numbersFrequency.put("11", 2.0);
        numbersFrequency.put("12", 1.0);
        return numbersFrequency;
    }
}
