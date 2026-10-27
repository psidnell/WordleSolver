package com.psidnell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchTest
{
    private static Stream<Arguments> provideMatchData() {
        return Stream.of(
                // solution, guess, toString
                Arguments.of("geeky", "geeky", "GEEKY GM EM EM KM YM E>=2 G>=1 K>=1 Y>=1"),
                Arguments.of("geeky", "egyee", "EGYEE E? G? Y? E? EX E>=2 G>=1 Y>=1"),
                Arguments.of("geeky", "macho", "MACHO MX AX CX HX OX !A !C !H !M !O"),
                Arguments.of("trace", "boast", "BOAST BX OX AM SX T? A>=1 T>=1 !B !O !S"),
                Arguments.of("geeky", "weeee", "WEEEE WX EM EM EX EX E>=2 !W"),
                Arguments.of("gleep", "weeee", "WEEEE WX EX EM EM EX E>=2 !W")
        );
    }
    @ParameterizedTest
    @MethodSource("provideMatchData")
    void testMatches(String solution, String guess, String expected) {
        assertEquals(expected, new Match(new Word(guess), new Word(solution)).toString());
    }
}
