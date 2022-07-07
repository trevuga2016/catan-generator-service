package com.generatecatanboard.service;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.Probability;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.domain.Token;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GeneratorService {

    private final CDAClient cdaClient;
    private final ApplicationContext applicationContext;
    public final Random random;
    private static final String DESERT = "Desert";

    public GeneratorService(CDAClient cdaClient, ApplicationContext applicationContext) throws NoSuchAlgorithmException {
        this.cdaClient = cdaClient;
        this.applicationContext = applicationContext;
        this.random = SecureRandom.getInstanceStrong();
    }

    public ScenarioProperties getScenarioProperties(String scenario) throws PropertiesNotFoundException {
        Collection<ScenarioProperties> propertiesCollection = cdaClient.observeAndTransform(ScenarioProperties.class).include(10).all().blockingFirst();
        ScenarioProperties scenarioProperties = propertiesCollection.stream()
                .filter(item -> scenario.equals(item.getScenarioUrl()))
                .findAny()
                .orElse(null);
        if (scenarioProperties == null) {
            throw new PropertiesNotFoundException("No scenario properties were returned from scenario '".concat(scenario).concat("'"));
        }
        return addBackgroundProps(scenarioProperties);
    }

    public ScenarioProperties addBackgroundProps(ScenarioProperties scenarioProperties) {
        CDAEntry entry = cdaClient.fetch(CDAEntry.class).one(scenarioProperties.getContentfulId());
        CDAEntry backgroundProps = entry.getField("backgroundProps");
        CDAAsset backgroundImage = backgroundProps.getField("backgroundImage");
        String backgroundColor = backgroundProps.getField("backgroundColor");
        scenarioProperties.setBackgroundImage("https:".concat(backgroundImage.fileField("url")).concat("?fm=webp"));
        scenarioProperties.setBackgroundColor(backgroundColor);
        return scenarioProperties;
    }

    public BoardData generateRandomBoard(String scenario, String harbors) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        Generator generator = applicationContext.getBean(harbors, Generator.class);
        ScenarioProperties scenarioProperties = getScenarioProperties(scenario);
        List<String> numbersList = getListOfNumberedItems(scenarioProperties.getNumbersFrequency());
        List<String> resourcesList = getListOfNumberedItems(scenarioProperties.getResourcesFrequency());
        List<Double> rowConfig = scenarioProperties.getRowConfig();
        GameHarborConfig gameHarborConfig = scenarioProperties.getGameHarborConfig();
        validateConfiguration(resourcesList, rowConfig);
        return generator.generateRandomBoard(rowConfig, resourcesList, numbersList, gameHarborConfig);
    }

    public List<CDAEntry> getBuildingCosts(String scenario) throws PropertiesNotFoundException {
        return null;
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

    public void validateConfiguration(List<String> resourcesList, List<Double> rowConfig) throws InvalidBoardConfigurationException {
        Integer totalNumberOfHexes = getTotalNumberOfHexes(rowConfig);
        if (resourcesList.size() != totalNumberOfHexes) {
            throw new InvalidBoardConfigurationException("The total number of hexes (".concat(String.valueOf(totalNumberOfHexes)).concat(") does not match the total number of provided resources (".concat(String.valueOf(resourcesList.size())).concat(")")));
        }
    }

    public Integer getTotalNumberOfHexes(List<Double> rowConfig) {
        return rowConfig.stream().mapToInt(Double::intValue).sum();
    }

    public List<Hex> getRowsOfResourceHexes(Double r, List<String> resourcesList, List<String> numbersList) {
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
        return hexes;
    }

    public int getSizeOfFirstRow(List<Double> rowConfig) {
        return rowConfig.get(0).intValue();
    }

    public int getSizeOfLastRow(List<Double> rowConfig) {
        return rowConfig.get(rowConfig.size() - 1).intValue();
    }

    public void validateHarborConfiguration(List<Double> rowConfig, GameHarborConfig gameHarborConfig) throws InvalidBoardConfigurationException {
        if (gameHarborConfig == null || gameHarborConfig.getHarborConfig() == null) {
            throw new InvalidBoardConfigurationException("The harbors for this scenario have not been properly configured. Please check the harbors game board configuration");
        }
        AtomicInteger expectedNoOfHarbors = new AtomicInteger(0);
        int noOfBoardRows = rowConfig.size();
        int noFirstRowHexes = getSizeOfFirstRow(rowConfig);
        int noLastRowHexes = getSizeOfLastRow(rowConfig);
        expectedNoOfHarbors.set(expectedNoOfHarbors.get() + noFirstRowHexes + 1);
        expectedNoOfHarbors.set(expectedNoOfHarbors.get() + noLastRowHexes + 1);
        for (int i = 0; i < noOfBoardRows; i++) {
            expectedNoOfHarbors.set(expectedNoOfHarbors.get() + 2);
        }
        if (gameHarborConfig.getHarborConfig().size() != expectedNoOfHarbors.get()) {
            throw new InvalidBoardConfigurationException("The expected number of harbor configs for this scenario ("
                    .concat(expectedNoOfHarbors.toString()).concat(") does not equal the provided number of harbor configs (")
                    .concat(String.valueOf(gameHarborConfig.getHarborConfig().size())).concat(")"));
        }
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
