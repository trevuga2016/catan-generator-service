package com.generatecatanboard.controller;

import com.generatecatanboard.domain.DexNavRequest;
import com.generatecatanboard.domain.DexNavResponse;
import com.generatecatanboard.service.DexNavService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DexNavController {

    private final DexNavService dexNavService;

    public DexNavController(DexNavService dexNavService) {
        this.dexNavService = dexNavService;
    }

    @PostMapping(value = "/dexnav/shinyOdds", consumes = "application/json", produces = "application/json")
    public DexNavResponse getShinyOdds(@Valid @RequestBody DexNavRequest dexNavRequest) {
        return dexNavService.getShinyOdds(dexNavRequest);
    }
}
