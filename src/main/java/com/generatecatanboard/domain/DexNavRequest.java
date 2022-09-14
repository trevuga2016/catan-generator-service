package com.generatecatanboard.domain;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class DexNavRequest {
    @Min(value = 1, message = "Minimum search level is 1")
    @Max(value = 999, message = "Maximum search level is 999")
    private int searchLevel;
    private boolean hasShinyCharm = false;
    @Min(value = 0, message = "Minimum chain value is 0")
    private int chain;
}
