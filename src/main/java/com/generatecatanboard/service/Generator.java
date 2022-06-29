package com.generatecatanboard.service;

import com.generatecatanboard.domain.BoardData;
import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;

public interface Generator {
    BoardData generateRandomBoard(String scenario) throws PropertiesNotFoundException, InvalidBoardConfigurationException;
}
