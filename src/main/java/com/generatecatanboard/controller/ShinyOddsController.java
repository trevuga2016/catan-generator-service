package com.generatecatanboard.controller;

import com.generatecatanboard.domain.ShinyOddsRequest;
import com.generatecatanboard.domain.ShinyOddsResponse;
import com.generatecatanboard.service.ShinyOddsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ShinyOddsController {

    private final ShinyOddsService shinyOddsService;

    public ShinyOddsController(ShinyOddsService shinyOddsService) {
        this.shinyOddsService = shinyOddsService;
    }

    @PostMapping(value = "/dexnav/shinyOdds", consumes = "application/json", produces = "application/json")
    public ShinyOddsResponse getShinyOdds(@Valid @RequestBody ShinyOddsRequest shinyOddsRequest) {
        return shinyOddsService.getShinyOdds(shinyOddsRequest);
    }

    @PostMapping(value = "/sos/shinyOdds", consumes = "application/json", produces = "application/json")
    public ShinyOddsResponse getSosShinyOdds(@Valid @RequestBody ShinyOddsRequest sosRequest) {
        return shinyOddsService.getSosShinyOdds(sosRequest);
    }
}
