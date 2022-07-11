package com.generatecatanboard.domain;

import com.contentful.java.cda.TransformQuery;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@TransformQuery.ContentfulEntryModel("buildingCosts")
public class BuildingCosts {
    @TransformQuery.ContentfulField
    private String buildType;
    @TransformQuery.ContentfulField
    private List<Resources> resources;
    @TransformQuery.ContentfulField
    private String victoryPoints;
}
