package com.generatecatanboard.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShinyOddsResponse {
    private String rawShinyOdds;
    private String rawMaxShinyOdds;
    private Integer shinyOdds;
    private Integer maxShinyOdds;
}
