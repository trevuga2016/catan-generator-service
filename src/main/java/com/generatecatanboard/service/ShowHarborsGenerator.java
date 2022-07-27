package com.generatecatanboard.service;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.GameProperties;
import com.generatecatanboard.domain.HarborConfig;
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

@Service(value = "show")
public class ShowHarborsGenerator extends GeneratorService implements Generator {

    protected ShowHarborsGenerator(CDAClient cdaClient, ApplicationContext applicationContext) throws NoSuchAlgorithmException {
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
        // Get hexes
        List<Rows> rowsOfHexes = new ArrayList<>();
        rowsOfHexes.add(createRowOfHarbors(harborConfigs, getSizeOfFirstRow(rowConfig) + 1));
        rowsOfHexes.addAll(createRowsOfHexesWithHarbors(scenarioProperties, harborConfigs));
        rowsOfHexes.add(createRowOfHarbors(harborConfigs, getSizeOfLastRow(rowConfig) + 1));
        // Get statistics
        List<Statistics> statistics = calculateBoardStatistics(rowsOfHexes, scenarioProperties);
        return BoardData.builder().gameProperties(gameProperties).gameBoard(rowsOfHexes).gameStatistics(statistics).build();
    }

    public Rows createRowOfHarbors(List<HarborConfig> harborConfigs, int sizeOfRow) {
        List<Hex> harborsHexes = new ArrayList<>();
        for (int i = 0; i < sizeOfRow; i++) {
            harborsHexes.add(getFirstHexFromHarborConfigs(harborConfigs));
        }
        return Rows.builder().row(harborsHexes).build();
    }

    public List<Rows> createRowsOfHexesWithHarbors(ScenarioProperties scenarioProperties, List<HarborConfig> harborConfigs) {
        List<Rows> listOfRows = new ArrayList<>();
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        for (Double r: rowConfig) {
            List<Hex> hexes = new ArrayList<>();
            hexes.add(getFirstHexFromHarborConfigs(harborConfigs));
            hexes.addAll(getRowsOfResourceHexes(r, scenarioProperties));
            hexes.add(getFirstHexFromHarborConfigs(harborConfigs));
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        }
        return listOfRows;
    }

    public Hex getFirstHexFromHarborConfigs(List<HarborConfig> harborConfigs) {
        HarborConfig harborConfig = harborConfigs.get(0);
        String resource = harborConfig.getHarborType().getId();
        String terrain = harborConfig.getHarborType().getTerrain();
        String rotation = harborConfig.getRotation();
        String hexImage = getHarborHexImage(harborConfig.getHarborType().getHexImageAsset(), rotation);
        CDAAsset hexCardImageAsset = harborConfig.getHarborType().getCardImageAsset();
        String hexCardImage = CDA_PREFIX.concat(hexCardImageAsset.fileField("url")).concat(CDA_WEBP_SUFFIX);
        HexCard hexCard = HexCard.builder().image(hexCardImage).subtext(harborConfig.getHarborType().getType()).description(harborConfig.getHarborType().getDescription()).build();
        harborConfigs.remove(harborConfig);
        return Hex.builder().resource(resource).terrain(terrain).rotation(rotation).hexImage(hexImage).hexCard(hexCard).build();
    }
}
