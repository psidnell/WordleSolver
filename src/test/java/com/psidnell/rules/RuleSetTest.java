package com.psidnell.rules;

import com.psidnell.Word;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleSetTest
{
    private static Stream<Arguments> provideMatchData() {
        return Stream.of(
                // solution, guess, toString
                Arguments.of("geeky", "geeky", "GM[0] EM[1] EM[2] KM[3] YM[4] E>=2 G>=1 K>=1 Y>=1"),
                Arguments.of("geeky", "egyee", "E?[0] G?[1] Y?[2] E?[3] EX[4] E>=2 G>=1 Y>=1"),
                Arguments.of("geeky", "macho", "MX[0] AX[1] CX[2] HX[3] OX[4] A! C! H! M! O!"),
                Arguments.of("trace", "boast", "BX[0] OX[1] AM[2] SX[3] T?[4] B! O! S! A>=1 T>=1"),
                Arguments.of("geeky", "weeee", "WX[0] EM[1] EM[2] EX[3] EX[4] W! E>=2"),
                Arguments.of("aaaaa", "aazzz", "AM[0] AM[1] ZX[2] ZX[3] ZX[4] Z! A>=2")
        );
    }
    @ParameterizedTest
    @MethodSource("provideMatchData")
    void testMatches(String solution, String guess, String expected) {
        assertEquals(expected, new RuleSet(new Word(guess), new Word(solution)).toString());
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
        RuleSet m = new RuleSet(new Word(guess), new Word(solution));
        assertEquals(eliminated, !m.match(new Word(elimWord)));
    }
}
