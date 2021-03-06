package com.generatecatanboard.exceptions;

public class PropertiesNotFoundException extends Exception {

    private final String message;

    public PropertiesNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
