package com.generatecatanboard.service;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.Probability;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.domain.Token;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service(value = "hide")
public class HideHarborsGenerator implements Generator {

    private final GeneratorService generatorService;
    private final Random random;
    private static final String DESERT = "Desert";

    public HideHarborsGenerator(GeneratorService generatorService) throws NoSuchAlgorithmException {
        this.generatorService = generatorService;
        this.random = SecureRandom.getInstanceStrong();
    }

    @Override
    public BoardData generateRandomBoard(String scenario) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        ScenarioProperties scenarioProperties = generatorService.getScenarioProperties(scenario);
        List<String> numbersList = getListOfNumberedItems(scenarioProperties.getNumbersFrequency());
        List<String> resourcesList = getListOfNumberedItems(scenarioProperties.getResourcesFrequency());
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        validateConfiguration(resourcesList, rowConfig);
        List<Rows> rowsOfHexes = createRowsOfHexes(rowConfig, resourcesList, numbersList);
        return BoardData.builder().gameBoard(rowsOfHexes).build();
    }

    public List<String> getListOfNumberedItems(Map<String, Double> numbersFrequency) {
        List<String> numbersList = new ArrayList<>();
        numbersFrequency.forEach((k, v) -> {
            for (int i = 1; i <= v; i++) {
                numbersList.add(k);
            }
        });
        return numbersList;
    }

    public Integer getTotalNumberOfHexes(List<Double> rowConfig) {
        return rowConfig.stream().mapToInt(Double::intValue).sum();
    }

    public void validateConfiguration(List<String> resourcesList, List<Double> rowConfig) throws InvalidBoardConfigurationException {
        Integer totalNumberOfHexes = getTotalNumberOfHexes(rowConfig);
        if (resourcesList.size() != totalNumberOfHexes) {
            throw new InvalidBoardConfigurationException("The total number of hexes (".concat(String.valueOf(totalNumberOfHexes)).concat(") does not match the total number of provided resources (".concat(String.valueOf(resourcesList.size())).concat(")")));
        }
    }

    public List<Rows> createRowsOfHexes(List<Double> rowConfig, List<String> resourcesList, List<String> numbersList) {
        List<Rows> listOfRows = new ArrayList<>();
        rowConfig.forEach(r -> {
            List<Hex> hexes = new ArrayList<>();
            for (int i = 1; i <= r; i++) {
                int randomResourceIndex = this.random.nextInt(resourcesList.size());
                String resource = resourcesList.get(randomResourceIndex);
                if (DESERT.equals(resource)) {
                    Token token = Token.builder().probability(getNumberProbability("7")).build();
                    hexes.add(Hex.builder().resource(resource).terrain(getResourceTerrain(resource)).token(token).build());
                } else {
                    int randomNumberIndex = this.random.nextInt(numbersList.size());
                    String number = numbersList.get(randomNumberIndex);
                    Token token = Token.builder().number(number).probability(getNumberProbability(number)).build();
                    hexes.add(Hex.builder().resource(resource).terrain(getResourceTerrain(resource)).token(token).build());
                    numbersList.remove(randomNumberIndex);
                }
                resourcesList.remove(randomResourceIndex);
            }
            Rows row = Rows.builder().row(hexes).build();
            listOfRows.add(row);
        });
        return listOfRows;
    }

    public Probability getNumberProbability(String number) {
        String probabilityText = getProbabilityText(number);
        String probabilityValue = getProbabilityValue(number);
        return Probability.builder().text(probabilityText).value(probabilityValue).build();
    }

    public String getProbabilityValue(String number) {
        if ("2".equals(number) || "12".equals(number)) {
            double prob = 1 / (double) 36;
            return String.format("%.2f", prob * 100);
        } else if ("3".equals(number) || "11".equals(number)) {
            double prob = 2 / (double) 36;
            return String.format("%.2f", prob * 100);
        } else if ("4".equals(number) || "10".equals(number)) {
            double prob = 3 / (double) 36;
            return String.format("%.2f", prob * 100);
        } else if ("5".equals(number) || "9".equals(number)) {
            double prob = 4 / (double) 36;
            return String.format("%.2f", prob * 100);
        } else if ("6".equals(number) || "8".equals(number)) {
            double prob = 5 / (double) 36;
            return String.format("%.2f", prob * 100);
        } else if ("7".equals(number)) {
            double prob = 6 / (double) 36;
            return String.format("%.2f", prob * 100);
        } else {
            return "";
        }
    }

    public String getProbabilityText(String number) {
        if ("2".equals(number) || "12".equals(number)) {
            return "\u2022";
        } else if ("3".equals(number) || "11".equals(number)) {
            return "\u2022\u2022";
        } else if ("4".equals(number) || "10".equals(number)) {
            return "\u2022\u2022\u2022";
        } else if ("5".equals(number) || "9".equals(number)) {
            return "\u2022\u2022\u2022\u2022";
        } else if ("6".equals(number) || "8".equals(number)) {
            return "\u2022\u2022\u2022\u2022\u2022";
        } else {
            return "";
        }
    }

    public String getResourceTerrain(String resource) {
        if ("Ore".equals(resource)) {
            return "Mountains";
        } else if ("Brick".equals(resource)) {
            return "Hills";
        } else if ("Lumber".equals(resource)) {
            return "Forests";
        } else if ("Grain".equals(resource)) {
            return "Fields";
        } else if ("Wool".equals(resource)) {
            return "Pastures";
        } else if (DESERT.equals(resource)) {
            return DESERT;
        } else {
            return "";
        }
    }

}
