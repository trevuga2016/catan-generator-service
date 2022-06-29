package com.generatecatanboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.Probability;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class HideHarborsGeneratorTest {

	@Mock
	private GeneratorService generatorService;
	@InjectMocks
	private HideHarborsGenerator generator;


	@Test
	void shouldGenerateRandomBoard() throws Exception {
		when(generatorService.getScenarioProperties(anyString())).thenReturn(getMockScenarioProperties());
		BoardData boardData = generator.generateRandomBoard("catan");
		assertNotNull(boardData);
		assertNotNull(boardData.getGameBoard());
		assertEquals(5, boardData.getGameBoard().size());
		assertNotNull(boardData.getGameBoard().get(0));
		assertNotNull(boardData.getGameBoard().get(0).getRow());
		assertEquals(3, boardData.getGameBoard().get(0).getRow().size());
	}

	@Test
	void shouldGetNumbersList() {
		List<String> numbersList = generator.getListOfNumberedItems(mockNumbersFrequency());
		assertNotNull(numbersList);
		assertEquals(18,  numbersList.size());
	}

	@Test
	void shouldGetResourcesList() {
		List<String> resourcesList = generator.getListOfNumberedItems(mockResourcesFrequency());
		assertNotNull(resourcesList);
		assertEquals(19,  resourcesList.size());
	}

	@Test
	void shouldGetNumberOfHexes() {
		List<Double> rowConfig = List.of(3.0, 4.0, 5.0, 4.0, 3.0);
		Integer totalNumberOfHexes = generator.getTotalNumberOfHexes(rowConfig);
		assertNotNull(totalNumberOfHexes);
		assertTrue(totalNumberOfHexes > 0);
		assertEquals(19, totalNumberOfHexes);
	}

	@Test
	void shouldValidateConfiguration() throws Exception {
		List<String> resourcesList = generator.getListOfNumberedItems(mockResourcesFrequency());
		generator.validateConfiguration(resourcesList, List.of(3.0, 4.0, 5.0, 4.0, 3.0));
	}

	@Test
	void shouldCatchInvalidConfiguration() {
		List<String> resourcesList = generator.getListOfNumberedItems(mockResourcesFrequency());
		resourcesList.remove(0);
		Exception exception = assertThrows(InvalidBoardConfigurationException.class, () -> {
			generator.validateConfiguration(resourcesList, List.of(3.0, 4.0, 5.0, 4.0, 3.0));
		});
		String expectedMessage = "The total number of hexes (19) does not match the total number of provided resources (18)";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	void shouldCreateRowsOfHexes() {
		List<String> resourcesList = generator.getListOfNumberedItems(mockResourcesFrequency());
		List<String> numbersList = generator.getListOfNumberedItems(mockNumbersFrequency());
		List<Rows> response = generator.createRowsOfHexes(List.of(3.0, 4.0, 5.0, 4.0, 3.0), resourcesList, numbersList);
		assertNotNull(response);
		assertEquals(5, response.size());
		assertNotNull(response.get(0));
		assertNotNull(response.get(0).getRow());
		assertEquals(3, response.get(0).getRow().size());
		assertNotNull(response.get(1));
		assertNotNull(response.get(1).getRow());
		assertEquals(4, response.get(1).getRow().size());
		assertNotNull(response.get(2));
		assertNotNull(response.get(2).getRow());
		assertEquals(5, response.get(2).getRow().size());
		assertNotNull(response.get(3));
		assertNotNull(response.get(3).getRow());
		assertEquals(4, response.get(3).getRow().size());
		assertNotNull(response.get(4));
		assertNotNull(response.get(4).getRow());
		assertEquals(3, response.get(4).getRow().size());
		assertEquals(0, resourcesList.size());
		assertEquals(0, numbersList.size());
	}

	@ParameterizedTest
	@ValueSource(strings = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
	void shouldGetProbabilityValue(String number) {
		String value = generator.getProbabilityValue(number);
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
		String value = generator.getProbabilityText(number);
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
		Probability probability = generator.getNumberProbability(number);
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
			assertEquals("", probability.getValue());
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"Ore", "Brick", "Lumber", "Grain", "Wool", "Desert", "Invalid"})
	void shouldGetResourceTerrain(String resource) {
		String terrain = generator.getResourceTerrain(resource);
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

	private Map<String, Double> mockNumbersFrequency() {
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

	private Map<String, Double> mockResourcesFrequency() {
		Map<String, Double> resourcesFrequency = new HashMap<>();
		resourcesFrequency.put("Ore", 3.0);
		resourcesFrequency.put("Wool", 4.0);
		resourcesFrequency.put("Grain", 4.0);
		resourcesFrequency.put("Brick", 3.0);
		resourcesFrequency.put("Lumber", 4.0);
		resourcesFrequency.put("Desert", 1.0);
		return resourcesFrequency;
	}

	private ScenarioProperties getMockScenarioProperties() throws Exception {
		ObjectMapper om = new ObjectMapper();
		return om.readValue(this.getClass().getClassLoader().getResourceAsStream("mocks/mockGameProps.json"), ScenarioProperties.class);
	}
}
