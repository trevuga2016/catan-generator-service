package com.generatecatanboard.client.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Fields {
    private String scenarioUrl;
    private Map<String, Integer> numbersFrequency;
    private Map<String, Integer> resourcesFrequency;
    private List<Integer> rowConfig;
    private PortConfig portConfig;
}
