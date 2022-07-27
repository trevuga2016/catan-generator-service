package com.generatecatanboard.service;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.HarborConfig;
import com.generatecatanboard.domain.Harbors;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.utility.ServiceTestBaseClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RandomizeHarborsGeneratorTest extends ServiceTestBaseClass {

	@Autowired
	private RandomizeHarborsGenerator randomizeHarborsGenerator;

	@Test
	void shouldGenerateRandomBoardWithRandomHarbors() throws Exception {
		BoardData boardData = randomizeHarborsGenerator.generateRandomBoard(getMockScenarioProps());
		assertNotNull(boardData);
		assertNotNull(boardData.getGameBoard());
		assertEquals(7, boardData.getGameBoard().size());
	}

	@Test
	void shouldCreateListOfRandomHarbors() throws Exception {
		GameHarborConfig gameHarborConfig = getMockGameHarborConfig();
		List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
		List<Harbors> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		assertNotNull(availableHarbors);
		assertEquals(9, availableHarbors.size());
	}

	@Test
	void shouldCreateRowOfRandomHarbors() throws Exception {
		ScenarioProperties properties = getMockScenarioProps();
		List<HarborConfig> harborConfigs = properties.getGameHarborConfig().getHarborConfig();
		List<Harbors> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		Rows rowOfRandomHarbors = randomizeHarborsGenerator.createRowOfRandomHarbors(harborConfigs, availableHarbors, 4);
		assertNotNull(rowOfRandomHarbors);
		assertNotNull(rowOfRandomHarbors.getRow());
		assertEquals(4, rowOfRandomHarbors.getRow().size());
		assertNotNull(rowOfRandomHarbors.getRow().get(0));
		assertEquals("Sea", rowOfRandomHarbors.getRow().get(0).getTerrain());
		assertNotNull(rowOfRandomHarbors.getRow().get(1));
		assertEquals("Harbor", rowOfRandomHarbors.getRow().get(1).getTerrain());
		assertNotNull(rowOfRandomHarbors.getRow().get(2));
		assertEquals("Sea", rowOfRandomHarbors.getRow().get(2).getTerrain());
		assertNotNull(rowOfRandomHarbors.getRow().get(3));
		assertEquals("Harbor", rowOfRandomHarbors.getRow().get(3).getTerrain());
	}

	@Test
	void shouldCreateRowOfHexesWithRandomHarbors() throws Exception {
		ScenarioProperties properties = getMockScenarioProps();
		List<HarborConfig> harborConfigs = properties.getGameHarborConfig().getHarborConfig();
		List<Harbors> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		List<Rows> listOfRows = randomizeHarborsGenerator.createRowsOfHexesWithRandomHarbors(getMockScenarioProps(), harborConfigs, availableHarbors);
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
		ScenarioProperties properties = getMockScenarioProps();
		List<HarborConfig> harborConfigs = properties.getGameHarborConfig().getHarborConfig();
		List<Harbors> availableHarbors = randomizeHarborsGenerator.createListOfAvailableHarbors(harborConfigs);
		AtomicInteger listSize = new AtomicInteger(9);
		assertEquals(9, availableHarbors.size());
		for (int i = 0; i < 9; i++) {
			Harbors randomHarbor = randomizeHarborsGenerator.getRandomHarbor(availableHarbors);
			assertNotNull(randomHarbor);
			listSize.set(listSize.get() - 1);
			assertEquals(listSize.get(), availableHarbors.size());
		}
		assertEquals(0, availableHarbors.size());
	}
}
