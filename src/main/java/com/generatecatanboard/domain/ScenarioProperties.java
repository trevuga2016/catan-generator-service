package com.generatecatanboard.domain;

import com.contentful.java.cda.TransformQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TransformQuery.ContentfulEntryModel("scenario")
public class ScenarioProperties {
    @TransformQuery.ContentfulSystemField("id")
    @JsonIgnore
    private String contentfulId;
    @TransformQuery.ContentfulField
    private String title;
    @TransformQuery.ContentfulField
    private String scenarioUrl;
    @TransformQuery.ContentfulField
    private Map<String, Double> numbersFrequency;
    @TransformQuery.ContentfulField
    private Map<String, Double> resourcesFrequency;
    @TransformQuery.ContentfulField
    private List<Double> rowConfig;
    @TransformQuery.ContentfulField
    private LinkedTreeMap<String, ?> portConfig;
    private String backgroundImage;
    private String backgroundColor;
}