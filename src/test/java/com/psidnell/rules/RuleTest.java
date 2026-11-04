package com.psidnell.rules;

import com.psidnell.words.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RuleTest {

    private static Stream<Arguments> provideMatchData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('G', 0, "XXXXG", false),
                Arguments.of('G', 0, "GXXXX", true)
        );
    }
    @ParameterizedTest
    @MethodSource("provideMatchData")
    void testMatch(Character c, int pos, String word, boolean match) {
        assertEquals(match, Rule.createMatch(c, pos).match(new Word(word)));
    }

    private static Stream<Arguments> provideNoMatchData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('G', 0, "XXXXG", true),
                Arguments.of('G', 0, "GXXXX", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideNoMatchData")
    void testNoMatch(Character c, int pos, String word, boolean match) {
        assertEquals(match, Rule.createNoMatch(c, pos).match(new Word(word)));
    }

    private static Stream<Arguments> provideWrongPlaceData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('G', 0, "XXXXX", true),
                Arguments.of('G', 0, "XXXXG", true),
                Arguments.of('G', 0, "GXXXX", false),
                Arguments.of('T', 0, "PXXXX", true)
        );
    }
    @ParameterizedTest
    @MethodSource("provideWrongPlaceData")
    void testWrongPlace(Character c, int pos, String word, boolean match) {
        assertEquals(match, Rule.createWrongPlace(c, pos).match(new Word(word)));
    }

    private static Stream<Arguments> provideMustNotHaveData() {
        return Stream.of(
                // Character, Position, Word, matches
                Arguments.of('G', 0, "XXXXX", true),
                Arguments.of('G', 0, "XXXXG", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideMustNotHaveData")
    void testMustNotHave(Character c, int pos, String word, boolean match) {
        assertEquals(match, Rule.createMustNotHave(c).match(new Word(word)));
    }

    @Test()
    void testCombineDifferentTypes() {
        Rule r = Rule.createMustNotHave('X');
        assertThrows(IllegalArgumentException.class, () -> r.combine(Rule.createNoMatch('X', 1)));
    }

    private static Stream<Arguments> provideMustHaveAtLeastData() {
        return Stream.of(
                // Character, Count, Word, matches
                Arguments.of('G', 0, "XXXXX", true),
                Arguments.of('G', 1, "XXXXX", false),
                Arguments.of('G', 1, "XXXXG", true),
                Arguments.of('G', 1, "XXXGG", true),
                Arguments.of('G', 2, "XXXGG", true),
                Arguments.of('G', 3, "XXXGG", false)
                //Arguments.of('g', "xxxxg", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideMustHaveAtLeastData")
    void testMustHaveAtLeast(Character c, int count, String word, boolean match) {
        assertEquals(match, Rule.createMustHaveAtLeast(c, count).match(new Word(word)));
    }

    private static Stream<Arguments> provideMustHaveAtMostData() {
        return Stream.of(
                // Character, Count, Word, matches
                Arguments.of('G', 0, "XXXXX", true),
                Arguments.of('G', 1, "XXXXX", true),
                Arguments.of('G', 1, "XXXXG", true),
                Arguments.of('G', 1, "XXXGG", false),
                Arguments.of('G', 2, "XXXGG", true),
                Arguments.of('G', 3, "XXXGG", true)
                //Arguments.of('g', "xxxxg", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideMustHaveAtMostData")
    void testMustHaveAtMost(Character c, int count, String word, boolean match) {
        assertEquals(match, Rule.createMustHaveAtMost(c, count).match(new Word(word)));
    }

    @Test()
    void testCombineMatch() {
        Rule r = Rule.createMatch('A', 3);
        Rule combined = r.combine(Rule.createMatch('A', 3));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    void testCombineMatchWithDifferentChars() {
        Rule r = Rule.createMatch('A', 3);
        Rule combined = r.combine(Rule.createNoMatch('B', 3));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchWithDifferentChars() {
        Rule r = Rule.createNoMatch('A', 3);
        Rule combined = r.combine(Rule.createNoMatch('B', 3));
        assertEquals("AB", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchWithSameChars() {
        Rule r = Rule.createNoMatch('A', 3);
        Rule combined = r.combine(Rule.createNoMatch('A', 3));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchMismatchedPositions() {
        Rule r = Rule.createNoMatch('A', 3);
        assertThrows(IllegalArgumentException.class, () -> r.combine(Rule.createNoMatch('B', 4)));
    }

    @Test()
    void testCombineMustNotHave() {
        Rule r = Rule.createMustNotHave('A');
        Rule combined = r.combine(Rule.createMustNotHave('B'));
        assertEquals("AB", combined.getCharactersAsString());
        assertEquals (-1, combined.getPosition());
    }

    @Test()
    void testCombineMustHaveAtLeast() {
        Rule r = Rule.createMustHaveAtLeast('A', 1);
        Rule combined = r.combine(Rule.createMustHaveAtLeast('A', 2));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals(2, combined.getAmount());
        assertEquals (-1, combined.getPosition());

        r = Rule.createMustHaveAtLeast('A', 2);
        combined = r.combine(Rule.createMustHaveAtLeast('A', 1));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals(2, combined.getAmount());
        assertEquals (-1, combined.getPosition());
    }

    @Test()
    void testCombineMustHaveAtMost() {
        Rule r = Rule.createMustHaveAtMost('A', 1);
        Rule combined = r.combine(Rule.createMustHaveAtMost('A', 2));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals(1, combined.getAmount());
        assertEquals (-1, combined.getPosition());

        r = Rule.createMustHaveAtMost('A', 2);
        combined = r.combine(Rule.createMustHaveAtMost('A', 1));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals(1, combined.getAmount());
        assertEquals (-1, combined.getPosition());
    }
}
