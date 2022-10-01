package com.generatecatanboard.dexnav;

import com.generatecatanboard.service.ShinyOddsService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShinyOddsServiceTest {

    @Autowired
    private ShinyOddsService shinyOddsService;

    @ParameterizedTest
    @MethodSource("searchLevelIntegers")
    void shouldReturnSearchLevelPoints(int searchLevel, int expectedPointValue) {
        int searchLevelPoints = shinyOddsService.getSearchLevelPoints(searchLevel);
        assertEquals(expectedPointValue, searchLevelPoints);
    }

    @ParameterizedTest
    @MethodSource("numberOfRolls")
    void shouldReturnNumberOfRolls(boolean hasShinyCharm, int chain, int expectedNumberOfRolls) {
        int numberOfRolls = shinyOddsService.getNumberOfRolls(hasShinyCharm, chain);
        assertEquals(expectedNumberOfRolls, numberOfRolls);
    }

    @ParameterizedTest
    @MethodSource("shinyOddsList")
    void shouldCalculateShinyOdds(int searchLevelPoints, int numberOfRolls, int expectedShinyOdds) {
        int shinyOdds = (int) Math.round(1 / shinyOddsService.calculateShinyOdds(searchLevelPoints, numberOfRolls));
        assertEquals(expectedShinyOdds, shinyOdds);
    }

    private static Stream<Arguments> searchLevelIntegers() {
        return Stream.of(
            Arguments.of(0, 0),
            Arguments.of(25, 150),
            Arguments.of(100, 600),
            Arguments.of(101, 602),
            Arguments.of(102, 604),
            Arguments.of(199, 798),
            Arguments.of(200, 800),
            Arguments.of(201, 801),
            Arguments.of(572, 1172),
            Arguments.of(999, 1599)
        );
    }

    private static Stream<Arguments> numberOfRolls() {
        return Stream.of(
            Arguments.of(false, 0, 1),
            Arguments.of(false, 49, 1),
            Arguments.of(false, 50, 6),
            Arguments.of(false, 51, 6),
            Arguments.of(false, 99, 6),
            Arguments.of(false, 100, 11),
            Arguments.of(false, 101, 11),
            Arguments.of(false, 999, 11),
            Arguments.of(true, 0, 3),
            Arguments.of(true, 49, 3),
            Arguments.of(true, 50, 8),
            Arguments.of(true, 51, 8),
            Arguments.of(true, 99, 8),
            Arguments.of(true, 100, 13),
            Arguments.of(true, 101, 13),
            Arguments.of(true, 999, 13)
        );
    }

    private static Stream<Arguments> shinyOddsList() {
        return Stream.of(
            Arguments.of(600, 1, 1667),
            Arguments.of(700, 3, 477),
            Arguments.of(700, 6, 239),
            Arguments.of(700, 8, 179),
            Arguments.of(700, 11, 130),
            Arguments.of(700, 13, 110)
        );
    }
}
