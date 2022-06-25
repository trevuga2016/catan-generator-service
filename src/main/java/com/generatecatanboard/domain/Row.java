package com.generatecatanboard.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Row {
    @JsonProperty(value = "row")
    private List<Hex> rows;
}
