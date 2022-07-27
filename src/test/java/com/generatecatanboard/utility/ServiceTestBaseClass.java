package com.generatecatanboard.utility;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.BuildingCosts;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.GameResourcesConfig;
import com.generatecatanboard.domain.ScenarioProperties;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        ScenarioProperties properties = objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockScenarioProps.json"), ScenarioProperties.class);
        CDAAsset mockCDAAsset = objectMapper.readValue(getClass().getResourceAsStream("/mocks/mockCDAAsset.json"), CDAAsset.class);
        List<CDAAsset> mockCDAAssets = List.of(mockCDAAsset);
        properties.getGameResourcesConfig().getResourcesFrequency().forEach(frequency ->
                frequency.getResource().setCardImageAsset(mockCDAAsset)
        );
        properties.getGameHarborConfig().getHarborConfig().forEach(config ->
                config.getHarborType().setHexImageAsset(mockCDAAssets)
        );
        properties.getGameHarborConfig().getHarborConfig().forEach(config ->
                config.getHarborType().setCardImageAsset(mockCDAAsset)
        );
        return properties;
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

    public String getMockBuildingCostsAsString() throws Exception {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("mocks/mockBuildingCosts.json");
    if (inputStream != null) {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
    return "";
}
}
