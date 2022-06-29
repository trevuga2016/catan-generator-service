package com.generatecatanboard.service;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GeneratorService {

    private final CDAClient cdaClient;
    private final ApplicationContext applicationContext;

    public GeneratorService(CDAClient cdaClient, ApplicationContext applicationContext) {
        this.cdaClient = cdaClient;
        this.applicationContext = applicationContext;
    }

    public ScenarioProperties getScenarioProperties(String scenario) throws PropertiesNotFoundException {
        Collection<ScenarioProperties> propertiesCollection = cdaClient.observeAndTransform(ScenarioProperties.class).all().blockingFirst();
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
        return generator.generateRandomBoard(scenario);
    }
}
