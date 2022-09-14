package com.generatecatanboard.service;

import com.generatecatanboard.domain.DexNavRequest;
import com.generatecatanboard.domain.DexNavResponse;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
public class DexNavService {

    DecimalFormat df = new DecimalFormat("#.####");

    public DexNavService() {
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public DexNavResponse getShinyOdds(DexNavRequest dexNavRequest) {
        // calc search level points
        int searchLevel = dexNavRequest.getSearchLevel();
        int searchLevelPoints = getSearchLevelPoints(searchLevel);
        // calc number of rolls
        int numberOfRolls = getNumberOfRolls(dexNavRequest.isHasShinyCharm(), dexNavRequest.getChain());
        // calc odds of a shiny
        double shinyOdds = calculateShinyOdds(searchLevelPoints, numberOfRolls);
        // calc max shiny odds - randomly an additional 4 rolls is added to the total number of rolls
        double maxShinyOdds = calculateShinyOdds(searchLevelPoints, numberOfRolls + 4);
        return DexNavResponse.builder()
                .rawShinyOdds(df.format(shinyOdds).concat("%"))
                .rawMaxShinyOdds(df.format(maxShinyOdds).concat("%"))
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
}
