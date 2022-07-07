package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.HarborConfig;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "randomize")
public class RandomizeHarborsGenerator extends GeneratorService implements Generator {

    protected RandomizeHarborsGenerator(CDAClient cdaClient, ApplicationContext applicationContext) throws NoSuchAlgorithmException {
        super(cdaClient, applicationContext);
    }

    @Override
    public BoardData generateRandomBoard(List<Double> rowConfig, List<String> resourcesList, List<String> numbersList, GameHarborConfig gameHarborConfig) throws InvalidBoardConfigurationException {
        validateHarborConfiguration(rowConfig, gameHarborConfig);
        List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
        List<String> listOfAvailableHarbors = createListOfAvailableHarbors(harborConfigs);
        List<Rows> rowsOfHexes = new ArrayList<>();
        rowsOfHexes.add(createRowOfRandomHarbors(harborConfigs, listOfAvailableHarbors, getSizeOfFirstRow(rowConfig) + 1));
        rowsOfHexes.addAll(createRowsOfHexesWithRandomHarbors(rowConfig, resourcesList, numbersList, harborConfigs, listOfAvailableHarbors));
        rowsOfHexes.add(createRowOfRandomHarbors(harborConfigs, listOfAvailableHarbors, getSizeOfLastRow(rowConfig) + 1));
        return BoardData.builder().gameBoard(rowsOfHexes).build();
    }

    public List<String> createListOfAvailableHarbors(List<HarborConfig> harborConfigs) {
        List<String> availableHarbors = new ArrayList<>();
        harborConfigs.forEach(harborConfig -> {
            if (!"Sea".equals(harborConfig.getHarborType().getTerrain())) {
                availableHarbors.add(harborConfig.getHarborType().getId());
            }
        });
        return availableHarbors;
    }

    public Rows createRowOfRandomHarbors(List<HarborConfig> harborConfigs, List<String> availableHarbors, int sizeOfRow) {
        List<Hex> harborsHexes = new ArrayList<>();
        for (int i = 0; i < sizeOfRow; i++) {
            harborsHexes.add(getHexFromRandomHarbor(harborConfigs, availableHarbors));
        }
        return Rows.builder().row(harborsHexes).build();
    }

    public List<Rows> createRowsOfHexesWithRandomHarbors(List<Double> rowConfig, List<String> resourcesList,
                                                         List<String> numbersList, List<HarborConfig> harborConfigs, List<String> availableHarbors) {
        List<Rows> listOfRows = new ArrayList<>();
        rowConfig.forEach(r -> {
            List<Hex> hexes = new ArrayList<>();
            hexes.add(getHexFromRandomHarbor(harborConfigs, availableHarbors));
            hexes.addAll(getRowsOfResourceHexes(r, resourcesList, numbersList));
            hexes.add(getHexFromRandomHarbor(harborConfigs, availableHarbors));
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        });
        return listOfRows;
    }

    public Hex getHexFromRandomHarbor(List<HarborConfig> harborConfigs, List<String> availableHarbors) {
        HarborConfig harborConfig = harborConfigs.get(0);
        String harborConfigTerrain = harborConfig.getHarborType().getTerrain();
        String resource = "Sea".equals(harborConfigTerrain) ? harborConfig.getHarborType().getId() : getRandomHarbor(availableHarbors);
        String rotation = harborConfig.getRotation();
        harborConfigs.remove(harborConfig);
        return Hex.builder().resource(resource).terrain(harborConfigTerrain).rotation(rotation).build();
    }

    public String getRandomHarbor(List<String> availableHarbors) {
        int randomHarborIndex = this.random.nextInt(availableHarbors.size());
        String harbor = availableHarbors.get(randomHarborIndex);
        availableHarbors.remove(randomHarborIndex);
        return harbor;
    }
}
