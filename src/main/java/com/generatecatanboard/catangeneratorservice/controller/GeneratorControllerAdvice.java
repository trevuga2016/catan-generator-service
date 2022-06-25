package com.generatecatanboard.catangeneratorservice.controller;

import com.generatecatanboard.catangeneratorservice.exceptions.PropertiesNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneratorControllerAdvice {

    @ExceptionHandler(value = PropertiesNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected Map<String, String> handlePropertiesNotFound(PropertiesNotFoundException exception) {
        Map<String, String> message = new HashMap<>();
        message.put("message", exception.getMessage());
        return message;
    }
}
