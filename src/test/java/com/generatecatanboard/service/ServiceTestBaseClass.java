package com.generatecatanboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generatecatanboard.domain.GameHarborConfig;

import java.util.HashMap;
import java.util.Map;

public class ServiceTestBaseClass {

    public GameHarborConfig getMockGameHarborConfig() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockGameHarborConfig.json"), GameHarborConfig.class);
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

    public Map<String, Double> mockResourcesFrequency() {
        Map<String, Double> resourcesFrequency = new HashMap<>();
        resourcesFrequency.put("Ore", 3.0);
        resourcesFrequency.put("Wool", 4.0);
        resourcesFrequency.put("Grain", 4.0);
        resourcesFrequency.put("Brick", 3.0);
        resourcesFrequency.put("Lumber", 4.0);
        resourcesFrequency.put("Desert", 1.0);
        return resourcesFrequency;
    }
}
