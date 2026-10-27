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
                Arguments.of("geeky", "geeky", "GM EM EM KM YM E>=2 G>=1 K>=1 Y>=1"),
                Arguments.of("geeky", "egyee", "E? G? Y? E? EX E>=2 G>=1 Y>=1"),
                Arguments.of("geeky", "macho", "MX AX CX HX OX !A !C !H !M !O"),
                Arguments.of("trace", "boast", "BX OX AM SX T? A>=1 T>=1 !B !O !S"),
                Arguments.of("geeky", "weeee", "WX EM EM EX EX E>=2 !W"),
                Arguments.of("aaaaa", "aazzz", "AM AM ZX ZX ZX A>=2 !Z")
        );
    }
    @ParameterizedTest
    @MethodSource("provideMatchData")
    void testMatches(String solution, String guess, String expected) {
        assertEquals(expected, new Match(new Word(guess), new Word(solution)).toString());
    }

    private static Stream<Arguments> provideEliminatehData() {
        return Stream.of(
                // solution, guess, elimWord, should eliminate
                Arguments.of("geeky", "geeky", "geeky", false),
                Arguments.of("geeky", "geeky", "feeky", true),
                Arguments.of("xxxxx", "yyyyy", "zzzzz", false),
                Arguments.of("aaaaa", "aazzz", "aabbb", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideEliminatehData")
    void testMWouldEliminate(String solution, String guess, String elimWord, boolean eliminated) {
        Match m = new Match(new Word(guess), new Word(solution));
        assertEquals(eliminated, m.wouldEliminateWord(new Word(elimWord)));
    }
}
