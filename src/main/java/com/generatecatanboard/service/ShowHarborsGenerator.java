package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.HarborConfig;
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

@Service(value = "show")
public class ShowHarborsGenerator extends GeneratorService implements Generator {

    protected ShowHarborsGenerator(CDAClient cdaClient, ApplicationContext applicationContext) throws NoSuchAlgorithmException {
        super(cdaClient, applicationContext);
    }

    @Override
    public BoardData generateRandomBoard(ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException {
        // Get configs
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        List<String> resourcesList = getListOfResources(scenarioProperties.getGameResourcesConfig());
        List<String> numbersList = getListOfNumberedItems(scenarioProperties.getNumbersFrequency());
        validateConfiguration(resourcesList, rowConfig);
        GameHarborConfig gameHarborConfig = scenarioProperties.getGameHarborConfig();
        validateHarborConfiguration(rowConfig, gameHarborConfig);
        List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
        // Get hexes
        List<Rows> rowsOfHexes = new ArrayList<>();
        rowsOfHexes.add(createRowOfHarbors(harborConfigs, getSizeOfFirstRow(rowConfig) + 1));
        rowsOfHexes.addAll(createRowsOfHexesWithHarbors(rowConfig, resourcesList, numbersList, harborConfigs));
        rowsOfHexes.add(createRowOfHarbors(harborConfigs, getSizeOfLastRow(rowConfig) + 1));
        // Get statistics
        List<Statistics> statistics = calculateBoardStatistics(rowsOfHexes, scenarioProperties);
        return BoardData.builder().gameBoard(rowsOfHexes).gameStatistics(statistics).build();
    }

    public Rows createRowOfHarbors(List<HarborConfig> harborConfigs, int sizeOfRow) {
        List<Hex> harborsHexes = new ArrayList<>();
        for (int i = 0; i < sizeOfRow; i++) {
            harborsHexes.add(getFirstHexFromHarborConfigs(harborConfigs));
        }
        return Rows.builder().row(harborsHexes).build();
    }

    public List<Rows> createRowsOfHexesWithHarbors(List<Double> rowConfig, List<String> resourcesList, List<String> numbersList, List<HarborConfig> harborConfigs) {
        List<Rows> listOfRows = new ArrayList<>();
        rowConfig.forEach(r -> {
            List<Hex> hexes = new ArrayList<>();
            hexes.add(getFirstHexFromHarborConfigs(harborConfigs));
            hexes.addAll(getRowsOfResourceHexes(r, resourcesList, numbersList));
            hexes.add(getFirstHexFromHarborConfigs(harborConfigs));
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        });
        return listOfRows;
    }

    public Hex getFirstHexFromHarborConfigs(List<HarborConfig> harborConfigs) {
        HarborConfig harborConfig = harborConfigs.get(0);
        String resource = harborConfig.getHarborType().getId();
        String terrain = harborConfig.getHarborType().getTerrain();
        String rotation = harborConfig.getRotation();
        harborConfigs.remove(harborConfig);
        return Hex.builder().resource(resource).terrain(terrain).rotation(rotation).build();
    }
}
