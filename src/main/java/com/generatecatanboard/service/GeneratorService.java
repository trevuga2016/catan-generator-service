package com.generatecatanboard.service;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.Commodities;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.domain.GameResourcesConfig;
import com.generatecatanboard.domain.Hex;
import com.generatecatanboard.domain.Probability;
import com.generatecatanboard.domain.Resources;
import com.generatecatanboard.domain.ResourcesFrequency;
import com.generatecatanboard.domain.Rows;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.domain.Statistics;
import com.generatecatanboard.domain.Token;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GeneratorService {

    private final CDAClient cdaClient;
    private final ApplicationContext applicationContext;
    public final Random random;
    private static final String DESERT = "Desert";
    private static final String CDA_PREFIX = "https:";
    private static final String CDA_WEBP_SUFFIX = "?fm=webp";

    public GeneratorService(CDAClient cdaClient, ApplicationContext applicationContext) throws NoSuchAlgorithmException {
        this.cdaClient = cdaClient;
        this.applicationContext = applicationContext;
        this.random = SecureRandom.getInstanceStrong();
    }

    public ScenarioProperties getScenarioProperties(String scenario) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        Collection<ScenarioProperties> propertiesCollection = cdaClient.observeAndTransform(ScenarioProperties.class).include(10).all().blockingFirst();
        ScenarioProperties scenarioProperties = propertiesCollection.stream()
                .filter(item -> scenario.equals(item.getScenarioUrl()))
                .findAny()
                .orElse(null);
        if (scenarioProperties == null) {
            throw new PropertiesNotFoundException("No scenario properties were returned from scenario '".concat(scenario).concat("'"));
        }
        setResourceIconUrls(scenarioProperties);
        setCommodityIconUrls(scenarioProperties);
        return addBackgroundProps(scenarioProperties);
    }

    public ScenarioProperties addBackgroundProps(ScenarioProperties scenarioProperties) {
        CDAEntry entry = cdaClient.fetch(CDAEntry.class).one(scenarioProperties.getContentfulId());
        CDAEntry backgroundProps = entry.getField("backgroundProps");
        CDAAsset backgroundImage = backgroundProps.getField("backgroundImage");
        String backgroundColor = backgroundProps.getField("backgroundColor");
        scenarioProperties.setBackgroundImage(CDA_PREFIX.concat(backgroundImage.fileField("url")).concat(CDA_WEBP_SUFFIX));
        scenarioProperties.setBackgroundColor(backgroundColor);
        return scenarioProperties;
    }

    public void setResourceIconUrls(ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException {
        assertNotNull(scenarioProperties.getGameResourcesConfig());
        assertNotNull(scenarioProperties.getGameResourcesConfig().getResourcesFrequency());
        List<ResourcesFrequency> resourcesFrequencies = scenarioProperties.getGameResourcesConfig().getResourcesFrequency();
        for (ResourcesFrequency frequency: resourcesFrequencies) {
            assertNotNull(frequency);
            Resources resources = frequency.getResource();
            assertNotNull(resources);
            if (!DESERT.equals(resources.getResource())) {
                CDAAsset iconAsset = resources.getIconAsset();
                resources.setIcon(CDA_PREFIX.concat(iconAsset.fileField("url")).concat(CDA_WEBP_SUFFIX));
            }
        }
    }

    public void setCommodityIconUrls(ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException {
        assertNotNull(scenarioProperties.getGameResourcesConfig());
        assertNotNull(scenarioProperties.getGameResourcesConfig().getResourcesFrequency());
        List<ResourcesFrequency> resourcesFrequencies = scenarioProperties.getGameResourcesConfig().getResourcesFrequency();
        for (ResourcesFrequency frequency: resourcesFrequencies) {
            assertNotNull(frequency);
            Resources resources = frequency.getResource();
            assertNotNull(resources);
            if (resources.getCommodity() != null) {
                Commodities commodity = resources.getCommodity();
                CDAAsset iconAsset = commodity.getIconAsset();
                commodity.setIcon(CDA_PREFIX.concat(iconAsset.fileField("url")).concat(CDA_WEBP_SUFFIX));
            }
        }
    }

    public BoardData generateRandomBoard(String scenario, String harbors) throws PropertiesNotFoundException, InvalidBoardConfigurationException {
        Generator generator = applicationContext.getBean(harbors, Generator.class);
        ScenarioProperties scenarioProperties = getScenarioProperties(scenario);
        return generator.generateRandomBoard(scenarioProperties);
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

    public List<String> getListOfResources(GameResourcesConfig gameResourcesConfig) {
        List<String> listOfResources = new ArrayList<>();
        List<ResourcesFrequency> resourcesFrequency = gameResourcesConfig.getResourcesFrequency();
        resourcesFrequency.forEach(frequency -> {
            for (int i = 0; i < frequency.getFrequency(); i++) {
                listOfResources.add(frequency.getResource().getResource());
            }
        });
        return listOfResources;
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

    public List<Statistics> calculateBoardStatistics(List<Rows> rows, ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException {
        List<Statistics> statistics = new ArrayList<>();
        assertNotNull(scenarioProperties.getGameResourcesConfig());
        assertNotNull(scenarioProperties.getGameResourcesConfig().getResourcesFrequency());
        List<ResourcesFrequency> resourcesFrequencies = scenarioProperties.getGameResourcesConfig().getResourcesFrequency();
        for (ResourcesFrequency frequency : resourcesFrequencies) {
            assertNotNull(frequency);
            assertNotNull(frequency.getResource());
            if (!DESERT.equals(frequency.getResource().getResource())) {
                String resource = frequency.getResource().getResource();
                List<String> numbers = new ArrayList<>();
                AtomicDouble probability = new AtomicDouble(0);
                for (Rows row : rows) {
                    for (Hex hex: row.getRow()) {
                        if (hex.getResource().equals(resource)) {
                            assertNotNull(hex.getToken());
                            numbers.add(hex.getToken().getNumber());
                        }
                    }
                }
                List<String> uniqueNumbers = numbers.stream().distinct().collect(Collectors.toList());
                uniqueNumbers.forEach(number -> {
                    double probValue = getProbabilityValue(number);
                    probability.set(probability.get() + probValue);
                });
                probability.set(probability.get() * 100);
                String probabilityString = String.format("%.2f", probability.get());
                Statistics statistic = Statistics.builder().build();
                assertNotNull(scenarioProperties.isCitiesAndKnights());
                if (scenarioProperties.isCitiesAndKnights() && frequency.getResource().getCommodity() != null) {
                    Commodities commodity = frequency.getResource().getCommodity();
                    statistic.setCommodity(commodity.getCommodity());
                    statistic.setCommodityIcon(commodity.getIcon());
                }
                statistic.setResource(resource);
                statistic.setProbability(probabilityString);
                statistic.setResourceIcon(frequency.getResource().getIcon());
                statistics.add(statistic);
            }
        }
        Comparator<Statistics> compareProb = Comparator.comparing(Statistics::getProbability);
        statistics.sort(compareProb.reversed());
        return statistics;
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
        String probabilityValue = String.format("%.2f", getProbabilityValue(number) * 100);
        return Probability.builder().text(probabilityText).value(probabilityValue).build();
    }

    public double getProbabilityValue(String number) {
        if ("2".equals(number) || "12".equals(number)) {
            return 1 / (double) 36;
        } else if ("3".equals(number) || "11".equals(number)) {
            return 2 / (double) 36;
        } else if ("4".equals(number) || "10".equals(number)) {
            return 3 / (double) 36;
        } else if ("5".equals(number) || "9".equals(number)) {
            return 4 / (double) 36;
        } else if ("6".equals(number) || "8".equals(number)) {
            return 5 / (double) 36;
        } else if ("7".equals(number)) {
            return 6 / (double) 36;
        } else {
            return 0;
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

    private static void assertNotNull(Object o) throws InvalidBoardConfigurationException {
        if (o == null) {
            throw new InvalidBoardConfigurationException("A null pointer exception occurred when trying to parse scenario properties. Please check your configuration.");
        }
    }
}
