package com.generatecatanboard.service;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.Rows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class HideHarborsGeneratorTest extends ServiceTestBaseClass {

	@Autowired
	public GeneratorService generatorService;
	@Autowired
	private HideHarborsGenerator hideHarborsGenerator;

	@Test
	void shouldCreateRandomBoardWithNoHarbors() {
		List<String> listOfResources = generatorService.getListOfNumberedItems(mockResourcesFrequency());
		List<String> listOfNumbers = generatorService.getListOfNumberedItems(mockNumbersFrequency());
		BoardData boardData = hideHarborsGenerator.generateRandomBoard(List.of(3.0, 4.0, 5.0, 4.0, 3.0), listOfResources, listOfNumbers, null);
		assertNotNull(boardData);
		assertNotNull(boardData.getGameBoard());
		assertEquals(5, boardData.getGameBoard().size());
	}

	@Test
	void shouldCreateRowsOfHexesWithoutHarbors() {
		List<String> listOfResources = generatorService.getListOfNumberedItems(mockResourcesFrequency());
		List<String> listOfNumbers = generatorService.getListOfNumberedItems(mockNumbersFrequency());
		List<Rows> rowsOfHexes = hideHarborsGenerator.createRowsOfHexes(List.of(3.0, 4.0, 5.0, 4.0, 3.0), listOfResources, listOfNumbers);
		assertNotNull(rowsOfHexes);
		assertNotNull(rowsOfHexes.get(0));
		assertNotNull(rowsOfHexes.get(0).getRow());
		assertEquals(3, rowsOfHexes.get(0).getRow().size());
		assertNotNull(rowsOfHexes.get(1));
		assertNotNull(rowsOfHexes.get(1).getRow());
		assertEquals(4, rowsOfHexes.get(1).getRow().size());
		assertNotNull(rowsOfHexes.get(2));
		assertNotNull(rowsOfHexes.get(2).getRow());
		assertEquals(5, rowsOfHexes.get(2).getRow().size());
		assertNotNull(rowsOfHexes.get(3));
		assertNotNull(rowsOfHexes.get(3).getRow());
		assertEquals(4, rowsOfHexes.get(3).getRow().size());
		assertNotNull(rowsOfHexes.get(4));
		assertNotNull(rowsOfHexes.get(4).getRow());
		assertEquals(3, rowsOfHexes.get(4).getRow().size());
	}
}
