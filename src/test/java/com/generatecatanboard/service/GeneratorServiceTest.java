package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class GeneratorServiceTest {

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
    private GeneratorService generatorService;

    @Test
    void shouldGetScenarioProperties() throws Exception {
        ScenarioProperties scenarioProperties = generatorService.getScenarioProperties("test");
        assertNotNull(scenarioProperties);
        assertEquals("The Test of Testan", scenarioProperties.getTitle());
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

}
