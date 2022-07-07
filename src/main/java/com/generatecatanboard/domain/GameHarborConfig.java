package com.generatecatanboard.domain;

import com.contentful.java.cda.TransformQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TransformQuery.ContentfulEntryModel("gameHarborConfig")
public class GameHarborConfig {
    @TransformQuery.ContentfulField
    private List<HarborConfig> harborConfig;
}
