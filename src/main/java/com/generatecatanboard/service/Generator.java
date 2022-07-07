package com.generatecatanboard.service;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.domain.GameHarborConfig;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;

import java.util.List;

public interface Generator {
    BoardData generateRandomBoard(List<Double> rowConfig, List<String> resourcesList, List<String> numbersList, GameHarborConfig gameHarborConfig) throws InvalidBoardConfigurationException;
}
