package com.generatecatanboard.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hex {
    private String resource;
    private String terrain;
    private Token token;
    private String rotation;
}
