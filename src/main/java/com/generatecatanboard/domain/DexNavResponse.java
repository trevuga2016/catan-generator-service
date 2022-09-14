package com.generatecatanboard.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DexNavResponse {
    private String rawShinyOdds;
    private String rawMaxShinyOdds;
    private Integer shinyOdds;
    private Integer maxShinyOdds;
}
