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
	private HideHarborsGenerator hideHarborsGenerator;

	@Test
	void shouldCreateRandomBoardWithNoHarbors() throws Exception {
		BoardData boardData = hideHarborsGenerator.generateRandomBoard(getMockScenarioProps());
		assertNotNull(boardData);
		assertNotNull(boardData.getGameBoard());
		assertEquals(5, boardData.getGameBoard().size());
	}

	@Test
	void shouldCreateRowsOfHexesWithoutHarbors() {
		List<Rows> rowsOfHexes = hideHarborsGenerator.createRowsOfHexes(getMockRowConfig(), getMockListOfResources(), getMockListOfNumbers());
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
