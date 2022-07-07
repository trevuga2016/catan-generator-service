package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.HarborConfig;
import com.generatecatanboard.domain.Rows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class RandomizeHarborsGeneratorTest extends ServiceTestBaseClass {

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
	@Autowired
	private RandomizeHarborsGenerator randomizeHarborsGenerator;

	@Test
	void shouldGenerateRandomBoardWithRandomHarbors() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<String> listOfResources = generatorService.getListOfNumberedItems(mockResourcesFrequency());
		List<String> listOfNumbers = generatorService.getListOfNumberedItems(mockNumbersFrequency());
		BoardData boardData = randomizeHarborsGenerator.generateRandomBoard(List.of(3.0, 4.0, 5.0, 4.0, 3.0), listOfResources, listOfNumbers, gameHarborConfig);
		assertNotNull(boardData);
		assertNotNull(boardData.getGameBoard());
		assertEquals(7, boardData.getGameBoard().size());
	}

	@Test
	void shouldCreateListOfRandomHarbors() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		List<String> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		assertNotNull(availableHarbors);
		assertEquals(9, availableHarbors.size());
	}

	@Test
	void shouldCreateRowOfRandomHarbors() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		List<String> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		Rows rowOfRandomHarbors = randomizeHarborsGenerator.createRowOfRandomHarbors(harborConfigs, availableHarbors, 4);
		assertNotNull(rowOfRandomHarbors);
		assertNotNull(rowOfRandomHarbors.getRow());
		assertEquals(4, rowOfRandomHarbors.getRow().size());
		assertNotNull(rowOfRandomHarbors.getRow().get(0));
		assertEquals("Sea", rowOfRandomHarbors.getRow().get(0).getTerrain());
		assertNotNull(rowOfRandomHarbors.getRow().get(1));
		assertEquals("Harbor", rowOfRandomHarbors.getRow().get(1).getTerrain());
		assertEquals("br", rowOfRandomHarbors.getRow().get(1).getRotation());
		assertNotNull(rowOfRandomHarbors.getRow().get(2));
		assertEquals("Sea", rowOfRandomHarbors.getRow().get(2).getTerrain());
		assertNotNull(rowOfRandomHarbors.getRow().get(3));
		assertEquals("Harbor", rowOfRandomHarbors.getRow().get(3).getTerrain());
		assertEquals("bl", rowOfRandomHarbors.getRow().get(3).getRotation());
	}

	@Test
	void shouldCreateRowOfHexesWithRandomHarbors() throws Exception {
		List<String> listOfResources = generatorService.getListOfNumberedItems(mockResourcesFrequency());
		List<String> listOfNumbers = generatorService.getListOfNumberedItems(mockNumbersFrequency());
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		List<String> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		List<Rows> listOfRows = randomizeHarborsGenerator.createRowsOfHexesWithRandomHarbors(List.of(3.0, 4.0, 5.0, 4.0, 3.0), listOfResources, listOfNumbers, harborConfigs, availableHarbors);
		assertNotNull(listOfRows);
		assertEquals(5, listOfRows.size());
		assertNotNull(listOfRows.get(0));
		assertNotNull(listOfRows.get(0).getRow());
		assertEquals(5, listOfRows.get(0).getRow().size());
		assertNotNull(listOfRows.get(1));
		assertNotNull(listOfRows.get(1).getRow());
		assertEquals(6, listOfRows.get(1).getRow().size());
		assertNotNull(listOfRows.get(2));
		assertNotNull(listOfRows.get(2).getRow());
		assertEquals(7, listOfRows.get(2).getRow().size());
		assertNotNull(listOfRows.get(3));
		assertNotNull(listOfRows.get(3).getRow());
		assertEquals(6, listOfRows.get(3).getRow().size());
		assertNotNull(listOfRows.get(4));
		assertNotNull(listOfRows.get(4).getRow());
		assertEquals(5, listOfRows.get(4).getRow().size());
	}

	@Test
	void shouldGetRandomHarborFromList() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		List<String> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		AtomicInteger listSize = new AtomicInteger(9);
		assertEquals(9, availableHarbors.size());
		for (int i = 0; i < 9; i++) {
			String randomHarbor = randomizeHarborsGenerator.getRandomHarbor(availableHarbors);
			assertNotNull(randomHarbor);
			listSize.set(listSize.get() - 1);
			assertEquals(listSize.get(), availableHarbors.size());
		}
		assertEquals(0, availableHarbors.size());
	}
}
