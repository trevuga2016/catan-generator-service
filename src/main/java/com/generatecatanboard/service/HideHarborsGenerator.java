package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.domain.Statistics;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "hide")
public class HideHarborsGenerator extends GeneratorService implements Generator {

    public HideHarborsGenerator(CDAClient cdaClient, ApplicationContext applicationContext) throws NoSuchAlgorithmException {
        super(cdaClient, applicationContext);
    }

    @Override
    public BoardData generateRandomBoard(ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException {
        // Get configs
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        List<String> resourcesList = getListOfResources(scenarioProperties.getGameResourcesConfig());
        List<String> numbersList = getListOfNumberedItems(scenarioProperties.getNumbersFrequency());
        validateConfiguration(resourcesList, rowConfig);
        // Get hexes
        List<Rows> rowsOfHexes = createRowsOfHexes(rowConfig, resourcesList, numbersList);
        // Get stats
        List<Statistics> statistics = calculateBoardStatistics(rowsOfHexes, scenarioProperties);
        return BoardData.builder().gameBoard(rowsOfHexes).gameStatistics(statistics).build();
    }

    public List<Rows> createRowsOfHexes(List<Double> rowConfig, List<String> resourcesList, List<String> numbersList) {
        List<Rows> listOfRows = new ArrayList<>();
        rowConfig.forEach(r -> {
            List<Hex> hexes = getRowsOfResourceHexes(r, resourcesList, numbersList);
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        });
        return listOfRows;
    }
}
