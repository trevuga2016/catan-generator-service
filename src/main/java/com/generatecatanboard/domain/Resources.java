package com.generatecatanboard.domain;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.TransformQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TransformQuery.ContentfulEntryModel("resources")
public class Resources {
    @TransformQuery.ContentfulField
    private String resource;
    @JsonIgnore
    @TransformQuery.ContentfulField(value = "icon")
    private CDAAsset iconAsset;
    private String icon;
    @JsonIgnore
    @TransformQuery.ContentfulField(value = "hexImage")
    private CDAAsset hexImageAsset;
    @TransformQuery.ContentfulField(value = "cardImage")
    private CDAAsset cardImageAsset;
    @TransformQuery.ContentfulField
    private Commodities commodity;
}
