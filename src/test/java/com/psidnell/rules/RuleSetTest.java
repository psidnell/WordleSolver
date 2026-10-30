package com.psidnell.rules;

import com.psidnell.Word;
import org.junit.jupiter.api.Test;
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
                Arguments.of("geeky", "geeky", "Matched{GEEKY} WrongPlace{_,_,_,_,_} NoMatch{_,_,_,_,_} None{} AtLeast{E:2,G:1,K:1,Y:1} AtMost{}"),
                Arguments.of("geeky", "egyee", "Matched{_____} WrongPlace{E,G,Y,E,_} NoMatch{_,_,_,_,E} None{} AtLeast{E:2,G:1,Y:1} AtMost{E:2}"),
                Arguments.of("geeky", "macho", "Matched{_____} WrongPlace{_,_,_,_,_} NoMatch{M,A,C,H,O} None{ACHMO} AtLeast{} AtMost{}"),
                Arguments.of("trace", "boast", "Matched{__A__} WrongPlace{_,_,_,_,T} NoMatch{B,O,_,S,_} None{BOS} AtLeast{A:1,T:1} AtMost{}"),
                Arguments.of("geeky", "weeee", "Matched{_EE__} WrongPlace{_,_,_,_,_} NoMatch{W,_,_,E,E} None{W} AtLeast{E:2} AtMost{E:2}"),
                Arguments.of("aaaaa", "aazzz", "Matched{AA___} WrongPlace{_,_,_,_,_} NoMatch{_,_,Z,Z,Z} None{Z} AtLeast{A:2} AtMost{}"),
                Arguments.of("hated", "dozed", "Matched{___ED} WrongPlace{D,_,_,_,_} NoMatch{_,O,Z,_,_} None{OZ} AtLeast{D:1,E:1} AtMost{D:1}"),
                Arguments.of("aaaab", "aaaaa", "Matched{AAAA_} WrongPlace{_,_,_,_,_} NoMatch{_,_,_,_,A} None{} AtLeast{A:4} AtMost{A:4}"),
                Arguments.of("baaaa", "aaaaa", "Matched{_AAAA} WrongPlace{A,_,_,_,_} NoMatch{_,_,_,_,_} None{} AtLeast{A:4} AtMost{A:4}")
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
                Arguments.of("geeky", "geeky", "geeky", true),
                Arguments.of("geeky", "geeky", "feeky", false),
                Arguments.of("xxxxx", "yyyyy", "zzzzz", true),
                Arguments.of("aaaaa", "aazzz", "aabbb", true),
                Arguments.of("aabbc", "bbxxx", "zzzbb", true),
                Arguments.of("abcde", "bcdea", "cdeab", true)
        );
    }
    @ParameterizedTest
    @MethodSource("provideEliminatehData")
    void testMWouldEliminate(String solution, String guess, String elimWord, boolean eliminated) {
        RuleSet m = new RuleSet(new Word(guess), new Word(solution));
        assertEquals(eliminated, m.match(new Word(elimWord)));
    }
}
