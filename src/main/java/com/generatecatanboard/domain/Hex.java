package com.generatecatanboard.domain;

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
public class Hex {
    private String resource;
    private String terrain;
    private Token token;
    @JsonIgnore
    private String rotation;
    private String hexImage;
    private HexCard hexCard;
}
