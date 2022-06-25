package com.generatecatanboard.client.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetEntriesResponse {
    private List<Items> items;
}
