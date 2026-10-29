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
                Arguments.of("geeky", "geeky", "Matched{GEEKY} WrongPlace{_,_,_,_,_} NoMatch{_,_,_,_,_} MustNotHave{} MustHaveAtLeast{E:2,G:1,K:1,Y:1}"),
                Arguments.of("geeky", "egyee", "Matched{_____} WrongPlace{E,G,Y,E,_} NoMatch{_,_,_,_,E} MustNotHave{} MustHaveAtLeast{E:2,G:1,Y:1}"),
                Arguments.of("geeky", "macho", "Matched{_____} WrongPlace{_,_,_,_,_} NoMatch{M,A,C,H,O} MustNotHave{ACHMO} MustHaveAtLeast{}"),
                Arguments.of("trace", "boast", "Matched{__A__} WrongPlace{_,_,_,_,T} NoMatch{B,O,_,S,_} MustNotHave{BOS} MustHaveAtLeast{A:1,T:1}"),
                Arguments.of("geeky", "weeee", "Matched{_EE__} WrongPlace{_,_,_,_,_} NoMatch{W,_,_,E,E} MustNotHave{W} MustHaveAtLeast{E:2}"),
                Arguments.of("aaaaa", "aazzz", "Matched{AA___} WrongPlace{_,_,_,_,_} NoMatch{_,_,Z,Z,Z} MustNotHave{Z} MustHaveAtLeast{A:2}")
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
