package com.generatecatanboard.service;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.GameProperties;
import com.generatecatanboard.domain.HarborConfig;
import com.generatecatanboard.domain.Harbors;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.HexCard;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.domain.Statistics;
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
    public BoardData generateRandomBoard(ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException {
        GameProperties gameProperties = GameProperties.builder().title(scenarioProperties.getTitle())
                .backgroundImage(scenarioProperties.getBackgroundImage()).backgroundColor(scenarioProperties.getBackgroundColor()).build();
        // Get configs
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        GameHarborConfig gameHarborConfig = scenarioProperties.getGameHarborConfig();
        validateHarborConfiguration(rowConfig, gameHarborConfig);
        List<HarborConfig> harborConfigs = gameHarborConfig.getHarborConfig();
        List<Harbors> listOfAvailableHarbors = createListOfAvailableHarbors(harborConfigs);
        // Get hexes
        List<Rows> rowsOfHexes = new ArrayList<>();
        rowsOfHexes.add(createRowOfRandomHarbors(harborConfigs, listOfAvailableHarbors, getSizeOfFirstRow(rowConfig) + 1));
        rowsOfHexes.addAll(createRowsOfHexesWithRandomHarbors(scenarioProperties, harborConfigs, listOfAvailableHarbors));
        rowsOfHexes.add(createRowOfRandomHarbors(harborConfigs, listOfAvailableHarbors, getSizeOfLastRow(rowConfig) + 1));
        // Get statistics
        List<Statistics> statistics = calculateBoardStatistics(rowsOfHexes, scenarioProperties);
        return BoardData.builder().gameProperties(gameProperties).gameBoard(rowsOfHexes).gameStatistics(statistics).build();
    }

    public List<Harbors> createListOfAvailableHarbors(List<HarborConfig> harborConfigs) {
        List<Harbors> availableHarbors = new ArrayList<>();
        harborConfigs.forEach(harborConfig -> {
            Harbors harbor = harborConfig.getHarborType();
            if (!"sea".equalsIgnoreCase(harbor.getTerrain())) {
                availableHarbors.add(harbor);
            }
        });
        return availableHarbors;
    }

    public Rows createRowOfRandomHarbors(List<HarborConfig> harborConfigs, List<Harbors> availableHarbors, int sizeOfRow) {
        List<Hex> harborsHexes = new ArrayList<>();
        for (int i = 0; i < sizeOfRow; i++) {
            harborsHexes.add(getHexFromRandomHarbor(harborConfigs, availableHarbors));
        }
        return Rows.builder().row(harborsHexes).build();
    }

    public List<Rows> createRowsOfHexesWithRandomHarbors(ScenarioProperties scenarioProperties, List<HarborConfig> harborConfigs, List<Harbors> availableHarbors) {
        List<Rows> listOfRows = new ArrayList<>();
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        for (Double r: rowConfig) {
            List<Hex> hexes = new ArrayList<>();
            hexes.add(getHexFromRandomHarbor(harborConfigs, availableHarbors));
            hexes.addAll(getRowsOfResourceHexes(r, scenarioProperties));
            hexes.add(getHexFromRandomHarbor(harborConfigs, availableHarbors));
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        }
        return listOfRows;
    }

    public Hex getHexFromRandomHarbor(List<HarborConfig> harborConfigs, List<Harbors> availableHarbors) {
        HarborConfig harborConfig = harborConfigs.get(0);
        Harbors harbor = harborConfig.getHarborType();
        String terrain = harbor.getTerrain();
        String rotation = harborConfig.getRotation();
        harborConfigs.remove(harborConfig);
        if ("sea".equalsIgnoreCase(terrain)) {
            String hexImage = getHarborHexImage(harborConfig.getHarborType().getHexImageAsset(), null);
            CDAAsset hexCardImageAsset = harbor.getCardImageAsset();
            String hexCardImage = CDA_PREFIX.concat(hexCardImageAsset.fileField("url")).concat(CDA_WEBP_SUFFIX);
            HexCard hexCard = HexCard.builder().image(hexCardImage).subtext(harbor.getType()).description(harbor.getDescription()).build();
            return Hex.builder().resource(harbor.getId()).terrain(terrain).hexImage(hexImage).hexCard(hexCard).build();
        } else {
            Harbors randomHarbor = getRandomHarbor(availableHarbors);
            String hexImage = getHarborHexImage(randomHarbor.getHexImageAsset(), rotation);
            CDAAsset hexCardImageAsset = randomHarbor.getCardImageAsset();
            String hexCardImage = CDA_PREFIX.concat(hexCardImageAsset.fileField("url")).concat(CDA_WEBP_SUFFIX);
            HexCard hexCard = HexCard.builder().image(hexCardImage).subtext(randomHarbor.getType()).description(randomHarbor.getDescription()).build();
            return Hex.builder().resource(randomHarbor.getId()).terrain(randomHarbor.getTerrain()).hexImage(hexImage).hexCard(hexCard).build();
        }
    }

    public Harbors getRandomHarbor(List<Harbors> availableHarbors) {
        int randomHarborIndex = this.random.nextInt(availableHarbors.size());
        Harbors harbor = availableHarbors.get(randomHarborIndex);
        availableHarbors.remove(randomHarborIndex);
        return harbor;
    }
}
