package com.generatecatanboard.client.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PortConfig {
    private List<Ports> top;
    private List<List<Ports>> ends;
    private List<Ports> bottom;
}
