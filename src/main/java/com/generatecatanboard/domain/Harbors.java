package com.generatecatanboard.domain;

import com.contentful.java.cda.TransformQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TransformQuery.ContentfulEntryModel("harbors")
public class Harbors {
    @TransformQuery.ContentfulField
    private String id;
    @TransformQuery.ContentfulField
    private String terrain;
}
