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
@JsonInclude(JsonInclude.Include.NON_NULL)
@TransformQuery.ContentfulEntryModel("commodities")
public class Commodities {
    @TransformQuery.ContentfulField
    private String commodity;
    @JsonIgnore
    @TransformQuery.ContentfulField(value = "icon")
    private CDAAsset iconAsset;
    private String icon;
}
