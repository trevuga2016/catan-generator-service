package com.generatecatanboard.domain;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.TransformQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@TransformQuery.ContentfulEntryModel("harbors")
public class Harbors {
    @TransformQuery.ContentfulField
    private String type;
    @TransformQuery.ContentfulField
    private String id;
    @TransformQuery.ContentfulField
    private String terrain;
    @TransformQuery.ContentfulField
    private String description;
    @JsonIgnore
    @TransformQuery.ContentfulField(value = "hexImage")
    private List<CDAAsset> hexImageAsset;
    @JsonIgnore
    @TransformQuery.ContentfulField(value = "cardImage")
    private CDAAsset cardImageAsset;
}
