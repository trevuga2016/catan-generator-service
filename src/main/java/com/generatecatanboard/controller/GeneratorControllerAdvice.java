package com.generatecatanboard.controller;

import com.generatecatanboard.exceptions.InvalidBoardConfigurationException;
import com.generatecatanboard.exceptions.PropertiesNotFoundException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneratorControllerAdvice {

    private static final String MESSAGE = "message";

    @ExceptionHandler(value = PropertiesNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected Map<String, String> handlePropertiesNotFound(PropertiesNotFoundException exception) {
        Map<String, String> message = new HashMap<>();
        message.put(MESSAGE, exception.getMessage());
        return message;
    }

    @ExceptionHandler(value = InvalidBoardConfigurationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected Map<String, String> handleInvalidBoardConfiguration(InvalidBoardConfigurationException exception) {
        Map<String, String> message = new HashMap<>();
        message.put(MESSAGE, exception.getMessage());
        return message;
    }

    @ExceptionHandler(value = NoSuchBeanDefinitionException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected Map<String, String> handleNoSuchBeanDefinitionException(NoSuchBeanDefinitionException exception) {
        Map<String, String> message = new HashMap<>();
        String beanName = exception.getBeanName();
        message.put(MESSAGE, "No configuration available for harbors: ".concat("'").concat(beanName).concat("'"));
        return message;
    }
}
