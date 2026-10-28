package com.psidnell.rules;

import com.psidnell.Word;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleTest {

    private static Stream<Arguments> provideMatchData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('g', 0, "xxxxg", false),
                Arguments.of('g', 0, "gxxxx", true)
        );
    }
    @ParameterizedTest
    @MethodSource("provideMatchData")
    void testMatch(Character c, int pos, String word, boolean match) {
        assertEquals(match, new Rule(RuleType.MATCH, c, pos).match(new Word(word)));
    }

    private static Stream<Arguments> provideNoMatchData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('g', 0, "xxxxg", true),
                Arguments.of('g', 0, "gxxxx", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideNoMatchData")
    void testNoMatch(Character c, int pos, String word, boolean match) {
        assertEquals(match, new Rule(RuleType.NO_MATCH, c, pos).match(new Word(word)));
    }

    private static Stream<Arguments> provideWrongPlaceData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('g', 0, "xxxxx", false),
                Arguments.of('g', 0, "xxxxg", true),
                Arguments.of('g', 0, "gxxxx", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideWrongPlaceData")
    void testWrongPlace(Character c, int pos, String word, boolean match) {
        assertEquals(match, new Rule(RuleType.WRONG_PLACE, c, pos).match(new Word(word)));
    }
}
