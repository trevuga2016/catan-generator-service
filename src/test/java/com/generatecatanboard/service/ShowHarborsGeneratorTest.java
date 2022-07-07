package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.HarborConfig;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.Rows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class ShowHarborsGeneratorTest extends ServiceTestBaseClass {

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
	private ShowHarborsGenerator showHarborsGenerator;

	@Test
	void shouldCreateRandomBoardWithHarbors() throws Exception {
		List<String> listOfResources = generatorService.getListOfNumberedItems(mockResourcesFrequency());
		List<String> listOfNumbers = generatorService.getListOfNumberedItems(mockNumbersFrequency());
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		BoardData boardData = showHarborsGenerator.generateRandomBoard(List.of(3.0, 4.0, 5.0, 4.0, 3.0), listOfResources, listOfNumbers, gameHarborConfig);
		assertNotNull(boardData);
		assertNotNull(boardData.getGameBoard());
		assertEquals(7, boardData.getGameBoard().size());
		assertNotNull(boardData.getGameBoard().get(0));
		assertNotNull(boardData.getGameBoard().get(0).getRow());
		assertEquals(4, boardData.getGameBoard().get(0).getRow().size());
		assertNotNull(boardData.getGameBoard().get(1));
		assertNotNull(boardData.getGameBoard().get(1).getRow());
		assertEquals(5, boardData.getGameBoard().get(1).getRow().size());
	}

	@Test
	void shouldCreateARowOfHarbors() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		assertEquals(18, harborConfigs.size());
		Rows rowOfHarbors = showHarborsGenerator.createRowOfHarbors(harborConfigs, 4);
		assertNotNull(rowOfHarbors);
		assertNotNull(rowOfHarbors.getRow());
		assertEquals(4, rowOfHarbors.getRow().size());
		assertNotNull(rowOfHarbors.getRow().get(0));
		assertEquals("sea", rowOfHarbors.getRow().get(0).getResource());
		assertEquals("Sea", rowOfHarbors.getRow().get(0).getTerrain());
		assertNotNull(rowOfHarbors.getRow().get(1));
		assertEquals("3for1", rowOfHarbors.getRow().get(1).getResource());
		assertEquals("Harbor", rowOfHarbors.getRow().get(1).getTerrain());
		assertEquals("br", rowOfHarbors.getRow().get(1).getRotation());
		assertNotNull(rowOfHarbors.getRow().get(2));
		assertEquals("sea", rowOfHarbors.getRow().get(2).getResource());
		assertEquals("Sea", rowOfHarbors.getRow().get(2).getTerrain());
		assertNotNull(rowOfHarbors.getRow().get(3));
		assertEquals("3for1", rowOfHarbors.getRow().get(3).getResource());
		assertEquals("Harbor", rowOfHarbors.getRow().get(3).getTerrain());
		assertEquals("bl", rowOfHarbors.getRow().get(3).getRotation());
		assertEquals(14, harborConfigs.size());
	}

	@Test
	void shouldCreateRowsOfHexesWithHarbors() throws Exception {
		List<String> listOfResources = generatorService.getListOfNumberedItems(mockResourcesFrequency());
		List<String> listOfNumbers = generatorService.getListOfNumberedItems(mockNumbersFrequency());
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		harborConfigs.subList(0, 4).clear();
		List<Rows> listOfHexesWithHarbors = showHarborsGenerator.createRowsOfHexesWithHarbors(List.of(3.0, 4.0, 5.0, 4.0, 3.0), listOfResources, listOfNumbers, harborConfigs);
		assertNotNull(listOfHexesWithHarbors);
		assertEquals(5, listOfHexesWithHarbors.size());
		assertNotNull(listOfHexesWithHarbors.get(0));
		assertNotNull(listOfHexesWithHarbors.get(0).getRow());
		assertEquals(5, listOfHexesWithHarbors.get(0).getRow().size());
		assertNotNull(listOfHexesWithHarbors.get(0).getRow().get(0));
		assertEquals("2for1_wool", listOfHexesWithHarbors.get(0).getRow().get(0).getResource());
		assertEquals("Harbor", listOfHexesWithHarbors.get(0).getRow().get(0).getTerrain());
		assertEquals("br", listOfHexesWithHarbors.get(0).getRow().get(0).getRotation());
		assertNotNull(listOfHexesWithHarbors.get(0).getRow().get(1));
		assertNotEquals("sea", listOfHexesWithHarbors.get(0).getRow().get(1).getResource());
		assertNotEquals("Sea", listOfHexesWithHarbors.get(0).getRow().get(1).getTerrain());
		assertNull(listOfHexesWithHarbors.get(0).getRow().get(1).getRotation());
	}

	@Test
	void shouldGetHexFromEndRowPortConfigs() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		assertEquals(18, harborConfigs.size());
		Hex hex1 = showHarborsGenerator.getFirstHexFromHarborConfigs(harborConfigs);
		assertEquals(17, harborConfigs.size());
		assertNotNull(hex1);
		assertEquals("sea", hex1.getResource());
		assertEquals("Sea", hex1.getTerrain());
		assertNull(hex1.getRotation());
		Hex hex2 = showHarborsGenerator.getFirstHexFromHarborConfigs(harborConfigs);
		assertEquals(16, harborConfigs.size());
		assertNotNull(hex2);
		assertEquals("3for1", hex2.getResource());
		assertEquals("Harbor", hex2.getTerrain());
		assertEquals("br", hex2.getRotation());
	}
}
