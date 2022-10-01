package com.generatecatanboard.service;

import com.generatecatanboard.domain.ShinyOddsRequest;
import com.generatecatanboard.domain.ShinyOddsResponse;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
public class ShinyOddsService {

    DecimalFormat df = new DecimalFormat("#.####");

    public ShinyOddsService() {
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public ShinyOddsResponse getShinyOdds(ShinyOddsRequest shinyOddsRequest) {
        // calc search level points
        int searchLevel = shinyOddsRequest.getSearchLevel();
        int searchLevelPoints = getSearchLevelPoints(searchLevel);
        // calc number of rolls
        int numberOfRolls = getNumberOfRolls(shinyOddsRequest.isHasShinyCharm(), shinyOddsRequest.getChain());
        // calc odds of a shiny
        double shinyOdds = calculateShinyOdds(searchLevelPoints, numberOfRolls);
        // calc max shiny odds - randomly an additional 4 rolls is added to the total number of rolls
        double maxShinyOdds = calculateShinyOdds(searchLevelPoints, numberOfRolls + 4);
        return ShinyOddsResponse.builder()
                .rawShinyOdds(df.format(shinyOdds * 100).concat("%"))
                .rawMaxShinyOdds(df.format(maxShinyOdds * 100).concat("%"))
                .shinyOdds((int) Math.round(1 / shinyOdds))
                .maxShinyOdds((int) Math.round(1 / maxShinyOdds)).build();
    }

    public int getSearchLevelPoints(int searchLevel) {
        if (searchLevel <= 100) {
            return searchLevel * 6;
        } else if (searchLevel <= 200) {
            return 600 + ((searchLevel - 100) * 2);
        } else {
            return 800 + (searchLevel - 200);
        }
    }

    public int getNumberOfRolls(boolean hasShinyCharm, int chain) {
        int numberOfRolls = hasShinyCharm ? 3 : 1;
        if (chain >= 50 && chain < 100) {
            return numberOfRolls + 5;
        } else if (chain >= 100) {
            return numberOfRolls + 10;
        } else {
            return numberOfRolls;
        }
    }

    public double calculateShinyOdds(int searchLevelPoints, int numberOfRolls) {
        double pointsAdjustment = 1 - (searchLevelPoints / 1000000d);
        double shinyOdds = 1- (Math.pow(pointsAdjustment, numberOfRolls));
        double minOdds = numberOfRolls / 4096d;
        return Math.max(shinyOdds, minOdds);
    }

    public ShinyOddsResponse getSosShinyOdds(ShinyOddsRequest request) {
        int numberOfRolls = getNumberOfSOSRolls(request.isHasShinyCharm(), request.getChain());
        double shinyOdds = calculateShinySOSOdds(numberOfRolls);
        return ShinyOddsResponse.builder()
                .rawShinyOdds(df.format(shinyOdds).concat("%"))
                .shinyOdds((int) Math.round(1 / shinyOdds))
                .build();
    }

    public int getNumberOfSOSRolls(boolean hasShinyCharm, int chain) {
        if (chain >= 11 && chain <= 20) {
            return hasShinyCharm ? 7 : 5;
        } else if (chain >= 21 && chain <=30) {
            return hasShinyCharm ? 11 : 9;
        } else if (chain >= 31) {
            return hasShinyCharm ? 15 : 13;
        } else {
            return hasShinyCharm ? 3 : 1;
        }
    }

    public double calculateShinySOSOdds(int numberOfRolls) {
        double normalOdds = 1 / 4096d;
        return 1 - (Math.pow((1 - normalOdds), numberOfRolls));
    }
}
