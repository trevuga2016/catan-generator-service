package com.generatecatanboard.service;

import com.contentful.java.cda.CDAClient;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameProperties;
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
        GameProperties gameProperties = GameProperties.builder().title(scenarioProperties.getTitle())
                .backgroundImage(scenarioProperties.getBackgroundImage()).backgroundColor(scenarioProperties.getBackgroundColor()).build();
        List<Rows> rowsOfHexes = createRowsOfHexes(scenarioProperties);
        List<Statistics> statistics = calculateBoardStatistics(rowsOfHexes, scenarioProperties);
        return BoardData.builder().gameProperties(gameProperties).gameBoard(rowsOfHexes).gameStatistics(statistics).build();
    }

    public List<Rows> createRowsOfHexes(ScenarioProperties scenarioProperties) {
        List<Rows> listOfRows = new ArrayList<>();
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        for (Double r: rowConfig) {
            List<Hex> hexes = getRowsOfResourceHexes(r, scenarioProperties);
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        }
        return listOfRows;
    }
}
