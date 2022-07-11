package com.generatecatanboard.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.BuildingCosts;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import com.generatecatanboard.service.GeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GeneratorController.class)
@WithMockUser(username = "${app.username}", password = "${app.password}", roles = "USER")
class GeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GeneratorService generatorService;

    @Test
    void shouldReturnScenarioProperties() throws Exception {
        when(generatorService.getScenarioProperties(anyString())).thenReturn(getMockScenarioProperties());
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(getMockScenarioProperties());
        this.mockMvc.perform(get("/scenarioProps/scenario"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    void shouldGenerateRandomBoard() throws Exception {
        when(generatorService.generateRandomBoard(anyString(), anyString())).thenReturn(getMockBoardData());
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(getMockBoardData());
        this.mockMvc.perform(get("/randomBoard/scenario").queryParam("harbors", "hide"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    void shouldHandlePropertiesNotFoundException() throws Exception {
        when(generatorService.getScenarioProperties(anyString())).thenThrow(new PropertiesNotFoundException("Properties were not found"));
        this.mockMvc.perform(get("/scenarioProps/scenario"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Properties were not found\"}"));
    }

    @Test
    void shouldHandleInvalidBoardConfigurationException() throws Exception {
        when(generatorService.generateRandomBoard(anyString(), anyString())).thenThrow(new InvalidBoardConfigurationException("Board was not configured correctly"));
        this.mockMvc.perform(get("/randomBoard/scenario").queryParam("harbors", "hide"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Board was not configured correctly\"}"));
    }

    @Test
    void shouldHandleNoSuchBeanDefinitionException() throws Exception {
        when(generatorService.generateRandomBoard(anyString(), anyString())).thenThrow(new NoSuchBeanDefinitionException("invalid"));
        this.mockMvc.perform(get("/randomBoard/scenario").queryParam("harbors", "invalid"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"No configuration available for harbors: 'invalid'\"}"));
    }

    @Test
    void shouldReturnBuildingCosts() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<BuildingCosts> buildingCosts = objectMapper.readValue(getMockBuildingCostsAsString(), new TypeReference<>(){});
        when(generatorService.getBuildingCosts(anyString())).thenReturn(buildingCosts);
        this.mockMvc.perform(get("/buildingCosts/scenario"))
                .andExpect(status().isOk())
                .andExpect(content().json(getMockBuildingCostsAsString()));
    }

    private ScenarioProperties getMockScenarioProperties() throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(this.getClass().getClassLoader().getResourceAsStream("mocks/mockScenarioProps.json"), ScenarioProperties.class);
    }

    private BoardData getMockBoardData() throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(this.getClass().getClassLoader().getResourceAsStream("mocks/mockBoardData.json"), BoardData.class);
    }

    private String getMockBuildingCostsAsString() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("mocks/mockBuildingCosts.json");
        if (inputStream != null) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return "";
    }
}
