package com.generatecatanboard.exceptions;

public class InvalidBoardConfigurationException extends Exception {

    private final String message;

    public InvalidBoardConfigurationException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
