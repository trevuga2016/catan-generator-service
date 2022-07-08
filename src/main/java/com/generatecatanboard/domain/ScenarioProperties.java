package com.generatecatanboard.domain;

import com.contentful.java.cda.TransformQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private List<Double> rowConfig;
    private String backgroundImage;
    private String backgroundColor;
    @TransformQuery.ContentfulField
    private GameHarborConfig gameHarborConfig;
    @TransformQuery.ContentfulField
    private GameResourcesConfig gameResourcesConfig;
    @TransformQuery.ContentfulField
    @JsonProperty(value = "isCitiesAndKnights")
    private boolean isCitiesAndKnights;
}
