package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.BuildingCosts;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.Probability;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.domain.Statistics;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import com.generatecatanboard.utility.ServiceTestBaseClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import wiremock.org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class GeneratorServiceTest extends ServiceTestBaseClass {

    @TestConfiguration
    static class TestConfig {
        @Value("${contentful.space}")
        private String contentfulSpace;
        @Value("${contentful.accessToken}")
        private String contentfulAccessToken;
        @Value("${wiremock.server.port}")
        private String wiremockPort;
        @Bean
        public CDAClient cdaClient() {
            return CDAClient.builder()
                    .setEndpoint("http://localhost:".concat(wiremockPort))
                    .setSpace(contentfulSpace)
                    .setToken(contentfulAccessToken)
                    .build();
        }
    }

    @Autowired
    public GeneratorService generatorService;

    @Test
    void shouldGetScenarioProperties() throws Exception {
        ScenarioProperties scenarioProperties = generatorService.getScenarioProperties("test");
        assertNotNull(scenarioProperties);
        assertEquals("The Settlers of Catan", scenarioProperties.getTitle());
        assertEquals("test", scenarioProperties.getScenarioUrl());
        assertEquals("abcdefg", scenarioProperties.getContentfulId());
        assertEquals("https://images.ctfassets.net/lemvlfz5icux/2JsBeiy2CdnHGVbbFQn73H/ddacfffaf63bc6b5895c2250ec877639/catan_backdrop.webp?fm=webp", scenarioProperties.getBackgroundImage());
        assertEquals("c21f26", scenarioProperties.getBackgroundColor());
    }

    @Test
    void shouldThrowPropertiesNotFoundException() {
        Exception exception = assertThrows(PropertiesNotFoundException.class, () -> {
            generatorService.getScenarioProperties("dummyScenario");
        });
        assertNotNull(exception);
        assertEquals("No scenario properties were returned from scenario 'dummyScenario'", exception.getMessage());
    }

    @Test
    void shouldAddBackgroundProps() {
        ScenarioProperties scenarioProperties = ScenarioProperties.builder().contentfulId("abcdefg").title("Test Scenario Properties").build();
        assertNull(scenarioProperties.getBackgroundImage());
        assertNull(scenarioProperties.getBackgroundColor());
        ScenarioProperties updatedProps = generatorService.addBackgroundProps(scenarioProperties);
        assertNotNull(updatedProps.getBackgroundImage());
        assertNotNull(updatedProps.getBackgroundColor());
        assertEquals("https://images.ctfassets.net/lemvlfz5icux/2JsBeiy2CdnHGVbbFQn73H/ddacfffaf63bc6b5895c2250ec877639/catan_backdrop.webp?fm=webp", updatedProps.getBackgroundImage());
        assertEquals("c21f26", updatedProps.getBackgroundColor());
    }

    @Test
    void shouldGenerateRandomBoard() throws Exception {
        BoardData boardData = generatorService.generateRandomBoard("test", "hide");
        assertNotNull(boardData);
        assertNotNull(boardData.getGameBoard());
        assertEquals(5, boardData.getGameBoard().size());
        assertNotNull(boardData.getGameBoard().get(0));
        assertNotNull(boardData.getGameBoard().get(0).getRow());
        assertEquals(3, boardData.getGameBoard().get(0).getRow().size());
    }

    @Test
    void shouldGetNumbersList() {
        List<String> numbersList = generatorService.getListOfNumberedItems(mockNumbersFrequency());
        assertNotNull(numbersList);
        assertEquals(18,  numbersList.size());
    }

    @Test
    void shouldGetResourcesList() throws Exception {
        List<String> resourcesList = generatorService.getListOfResources(getMockGameResourcesConfig());
        assertNotNull(resourcesList);
        assertEquals(19,  resourcesList.size());
    }

    @Test
    void shouldGetNumberOfHexes() {
        List<Double> rowConfig = List.of(3.0, 4.0, 5.0, 4.0, 3.0);
        Integer totalNumberOfHexes = generatorService.getTotalNumberOfHexes(rowConfig);
        assertNotNull(totalNumberOfHexes);
        assertTrue(totalNumberOfHexes > 0);
        assertEquals(19, totalNumberOfHexes);
    }

    @Test
    void shouldValidateConfiguration() throws Exception {
        List<String> resourcesList = generatorService.getListOfResources(getMockGameResourcesConfig());
        generatorService.validateConfiguration(resourcesList, List.of(3.0, 4.0, 5.0, 4.0, 3.0));
    }

    @Test
    void shouldCatchInvalidConfiguration() throws Exception {
        List<String> resourcesList = generatorService.getListOfResources(getMockGameResourcesConfig());
        resourcesList.remove(0);
        Exception exception = assertThrows(InvalidBoardConfigurationException.class, () -> {
            generatorService.validateConfiguration(resourcesList, List.of(3.0, 4.0, 5.0, 4.0, 3.0));
        });
        String expectedMessage = "The total number of hexes (19) does not match the total number of provided resources (18)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldValidateGameHarborConfig() throws Exception {
        GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
        assertNotNull(gameHarborConfig);
        assertNotNull(gameHarborConfig.getHarborConfig());
        assertEquals(18, getMockGameHarborConfig().getHarborConfig().size());
        generatorService.validateHarborConfiguration(List.of(3.0, 4.0, 5.0, 4.0, 3.0), getMockGameHarborConfig());
    }

    @Test
    void shouldThrowExceptionForMissingGameHarborConfig() {
        Exception exception = assertThrows(InvalidBoardConfigurationException.class, () -> {
            generatorService.validateHarborConfiguration(List.of(3.0, 4.0, 5.0, 4.0, 3.0), null);
        });
        assertNotNull(exception);
        assertEquals("The harbors for this scenario have not been properly configured. Please check the harbors game board configuration", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForMissingHarborConfigs() {
        GameHarborConfig gameHarborConfig = GameHarborConfig.builder().harborConfig(null).build();
        Exception exception = assertThrows(InvalidBoardConfigurationException.class, () -> {
            generatorService.validateHarborConfiguration(List.of(3.0, 4.0, 5.0, 4.0, 3.0), gameHarborConfig);
        });
        assertNotNull(exception);
        assertEquals("The harbors for this scenario have not been properly configured. Please check the harbors game board configuration", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForImproperlyConfiguredHarborConfigs() {
        Exception exception = assertThrows(InvalidBoardConfigurationException.class, () -> {
            generatorService.validateHarborConfiguration(List.of(3.0, 4.0, 5.0, 4.0, 4.0), getMockGameHarborConfig());
        });
        assertNotNull(exception);
        assertEquals("The expected number of harbor configs for this scenario (19) does not equal the provided number of harbor configs (18)", exception.getMessage());
    }

    @Test
    void shouldCalculateBoardStatistics() throws Exception {
        BoardData mockBoardData = getMockBoardData();
        List<Statistics> statistics = generatorService.calculateBoardStatistics(mockBoardData.getGameBoard(), getMockCitiesAndKnightsProps());
        assertNotNull(statistics);
        assertEquals(5, statistics.size());
        statistics.forEach(stat -> {
            if ("Lumber".equals(stat.getResource())) {
                assertEquals("Paper", stat.getCommodity());
                assertTrue(StringUtils.startsWith(stat.getCommodityIcon(), "https://"));
            } else if ("Wool".equals(stat.getResource())) {
                assertEquals("Cloth", stat.getCommodity());
                assertTrue(StringUtils.startsWith(stat.getCommodityIcon(), "https://"));
            } else if ("Ore".equals(stat.getResource())) {
                assertEquals("Coin", stat.getCommodity());
                assertTrue(StringUtils.startsWith(stat.getCommodityIcon(), "https://"));
            }
        });
    }

    @Test
    void shouldThrowExceptionForNullProperties() throws Exception {
        BoardData mockBoardData = getMockBoardData();
        ScenarioProperties scenarioProperties = ScenarioProperties.builder().gameResourcesConfig(null).build();
        Exception exception = assertThrows(InvalidBoardConfigurationException.class, () -> {
            generatorService.calculateBoardStatistics(mockBoardData.getGameBoard(), scenarioProperties);
        });
        assertEquals("A null pointer exception occurred when trying to parse scenario properties. Please check your configuration.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
    void shouldGetProbabilityValue(String number) {
        double dValue = generatorService.getProbabilityValue(number);
        String value = String.format("%.2f", dValue * 100);
        if ("2".equals(number) || "12".equals(number)) {
            assertEquals("2.78", value);
        } else if ("3".equals(number) || "11".equals(number)) {
            assertEquals("5.56", value);
        } else if ("4".equals(number) || "10".equals(number)) {
            assertEquals("8.33", value);
        } else if ("5".equals(number) || "9".equals(number)) {
            assertEquals("11.11", value);
        } else if ("6".equals(number) || "8".equals(number)) {
            assertEquals("13.89", value);
        } else if ("7".equals(number)) {
            assertEquals("16.67", value);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
    void shouldGetProbabilityText(String number) {
        String value = generatorService.getProbabilityText(number);
        if ("2".equals(number) || "12".equals(number)) {
            assertEquals("\u2022", value);
        } else if ("3".equals(number) || "11".equals(number)) {
            assertEquals("\u2022\u2022", value);
        } else if ("4".equals(number) || "10".equals(number)) {
            assertEquals("\u2022\u2022\u2022", value);
        } else if ("5".equals(number) || "9".equals(number)) {
            assertEquals("\u2022\u2022\u2022\u2022", value);
        } else if ("6".equals(number) || "8".equals(number)) {
            assertEquals("\u2022\u2022\u2022\u2022\u2022", value);
        } else if ("7".equals(number)) {
            assertEquals("", value);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"})
    void shouldGetProbability(String number) {
        Probability probability = generatorService.getNumberProbability(number);
        if ("2".equals(number) || "12".equals(number)) {
            assertEquals("\u2022", probability.getText());
            assertEquals("2.78", probability.getValue());
        } else if ("3".equals(number) || "11".equals(number)) {
            assertEquals("\u2022\u2022", probability.getText());
            assertEquals("5.56", probability.getValue());
        } else if ("4".equals(number) || "10".equals(number)) {
            assertEquals("\u2022\u2022\u2022", probability.getText());
            assertEquals("8.33", probability.getValue());
        } else if ("5".equals(number) || "9".equals(number)) {
            assertEquals("\u2022\u2022\u2022\u2022", probability.getText());
            assertEquals("11.11", probability.getValue());
        } else if ("6".equals(number) || "8".equals(number)) {
            assertEquals("\u2022\u2022\u2022\u2022\u2022", probability.getText());
            assertEquals("13.89", probability.getValue());
        } else if ("7".equals(number)) {
            assertEquals("", probability.getText());
            assertEquals("16.67", probability.getValue());
        } else {
            assertEquals("", probability.getText());
            assertEquals("0.00", probability.getValue());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ore", "Brick", "Lumber", "Grain", "Wool", "Desert", "Invalid"})
    void shouldGetResourceTerrain(String resource) {
        String terrain = generatorService.getResourceTerrain(resource);
        if ("Ore".equals(resource)) {
            assertEquals("Mountains", terrain);
        } else if ("Brick".equals(resource)) {
            assertEquals("Hills", terrain);
        } else if ("Lumber".equals(resource)) {
            assertEquals("Forests", terrain);
        } else if ("Grain".equals(resource)) {
            assertEquals("Fields", terrain);
        } else if ("Wool".equals(resource)) {
            assertEquals("Pastures", terrain);
        } else if ("Desert".equals(resource)) {
            assertEquals("Desert", terrain);
        } else {
            assertEquals("", terrain);
        }
    }

    @Test
    void shouldCleanBuildingCosts1() throws Exception {
        BuildingCosts cleanCost1 = generatorService.cleanBuildingCosts(getMockBuildingCost1());
        assertNotNull(cleanCost1);
        assertEquals("Road", cleanCost1.getBuildType());
        assertNotNull(cleanCost1.getResources());
        assertNotNull(cleanCost1.getResources().get(0));
        assertEquals("Lumber", cleanCost1.getResources().get(0).getResource());
        assertEquals("https://images.net/lumber", cleanCost1.getResources().get(0).getIcon());
        assertNull(cleanCost1.getResources().get(0).getCommodity());
        assertNotNull(cleanCost1.getResources().get(1));
        assertEquals("Brick", cleanCost1.getResources().get(1).getResource());
        assertEquals("https://images.net/brick", cleanCost1.getResources().get(1).getIcon());
        assertNull(cleanCost1.getResources().get(1).getCommodity());
        assertEquals("0 Points", cleanCost1.getVictoryPoints());
    }

    @Test
    void shouldCleanBuildingCosts2() throws Exception {
        BuildingCosts cleanCost2 = generatorService.cleanBuildingCosts(getMockBuildingCost2());
        assertNotNull(cleanCost2);
        assertEquals("Settlement", cleanCost2.getBuildType());
        assertNotNull(cleanCost2.getResources());
        assertNotNull(cleanCost2.getResources().get(0));
        assertEquals("Lumber", cleanCost2.getResources().get(0).getResource());
        assertEquals("https://images.net/lumber", cleanCost2.getResources().get(0).getIcon());
        assertNull(cleanCost2.getResources().get(0).getCommodity());
        assertNotNull(cleanCost2.getResources().get(1));
        assertEquals("Brick", cleanCost2.getResources().get(1).getResource());
        assertEquals("https://images.net/brick", cleanCost2.getResources().get(1).getIcon());
        assertNull(cleanCost2.getResources().get(1).getCommodity());
        assertNotNull(cleanCost2.getResources().get(2));
        assertEquals("Grain", cleanCost2.getResources().get(2).getResource());
        assertEquals("https://images.net/grain", cleanCost2.getResources().get(2).getIcon());
        assertNull(cleanCost2.getResources().get(2).getCommodity());
        assertNotNull(cleanCost2.getResources().get(3));
        assertEquals("Wool", cleanCost2.getResources().get(3).getResource());
        assertEquals("https://images.net/wool", cleanCost2.getResources().get(3).getIcon());
        assertNull(cleanCost2.getResources().get(3).getCommodity());
        assertEquals("1 Point", cleanCost2.getVictoryPoints());
    }

    @Test
    void shouldReturnBuildingCosts() throws Exception {
        List<BuildingCosts> buildingCosts = generatorService.getBuildingCosts("test");
        assertNotNull(buildingCosts);
        assertEquals(4, buildingCosts.size());
        assertNotNull(buildingCosts.get(0).getResources());
        assertNotNull(buildingCosts.get(0).getResources().get(0));
        assertNull(buildingCosts.get(0).getResources().get(0).getCommodity());
    }

    @ParameterizedTest
    @MethodSource("getListOfTestNumbers")
    void shouldGetProbabilityString(List<String> listOfStrings) {
        String probability = generatorService.getProbabilityString(listOfStrings.subList(0, listOfStrings.size() - 1));
        assertEquals(listOfStrings.get(listOfStrings.size() - 1), probability);
    }

    private static Stream<List<String>> getListOfTestNumbers() {
        return Stream.of(
                List.of("2", "3", "3", "08.33"),
                List.of("2", "12", "3", "11.11"),
                List.of("6", "8", "9", "5", "50.00")
        );
    }
}
