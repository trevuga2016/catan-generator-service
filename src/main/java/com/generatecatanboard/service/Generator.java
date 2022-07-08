package com.generatecatanboard.service;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.ScenarioProperties;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;

public interface Generator {
    BoardData generateRandomBoard(ScenarioProperties scenarioProperties) throws InvalidBoardConfigurationException;
}
