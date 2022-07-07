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
@TransformQuery.ContentfulEntryModel("harborConfig")
public class HarborConfig {
    @TransformQuery.ContentfulField
    private Harbors harborType;
    @TransformQuery.ContentfulField
    private String rotation;
}
