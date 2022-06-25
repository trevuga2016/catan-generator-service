package com.generatecatanboard.catangeneratorservice.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Row {
    private List<Hex> row;
}
