package com.psidnell.rules;

import com.psidnell.Word;
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
                Arguments.of('g', 0, "xxxxg", false),
                Arguments.of('g', 0, "gxxxx", true)
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
                Arguments.of('g', 0, "xxxxg", true),
                Arguments.of('g', 0, "gxxxx", false)
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
                Arguments.of('g', 0, "xxxxx", false),
                Arguments.of('g', 0, "xxxxg", true),
                Arguments.of('g', 0, "gxxxx", false)
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
                Arguments.of('g', 0, "xxxxx", true),
                Arguments.of('g', 0, "xxxxg", false)
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

    @Test()
    void testCombineMatch() {
        Rule r = Rule.createMatch('a', 3);
        Rule combined = r.combine(Rule.createMatch('a', 3));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    void testCombineMatchWithDifferentChars() {
        Rule r = Rule.createMatch('a', 3);
        Rule combined = r.combine(Rule.createNoMatch('b', 3));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchWithDifferentChars() {
        Rule r = Rule.createNoMatch('a', 3);
        Rule combined = r.combine(Rule.createNoMatch('b', 3));
        assertEquals("ab", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchWithSameChars() {
        Rule r = Rule.createNoMatch('a', 3);
        Rule combined = r.combine(Rule.createNoMatch('a', 3));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchMismatchedPositions() {
        Rule r = Rule.createNoMatch('a', 3);
        assertThrows(IllegalArgumentException.class, () -> r.combine(Rule.createNoMatch('b', 4)));
    }

    @Test()
    void testCombineMustNotHave() {
        Rule r = Rule.createMustNotHave('a');
        Rule combined = r.combine(Rule.createMustNotHave('b'));
        assertEquals("ab", combined.getCharactersAsString());
        assertEquals (-1, combined.getPosition());
    }

    @Test()
    void testCombineMustHaveAtLeast() {
        Rule r = Rule.createMustHaveAtLeast('a', 1);
        Rule combined = r.combine(Rule.createMustHaveAtLeast('a', 2));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals(2, combined.getAmount());
        assertEquals (-1, combined.getPosition());

        r = Rule.createMustHaveAtLeast('a', 2);
        combined = r.combine(Rule.createMustHaveAtLeast('a', 1));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals(2, combined.getAmount());
        assertEquals (-1, combined.getPosition());
    }

    @Test()
    void testCombineMustHaveAtMost() {
        Rule r = Rule.createMustHaveAtMost('a', 1);
        Rule combined = r.combine(Rule.createMustHaveAtMost('a', 2));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals(1, combined.getAmount());
        assertEquals (-1, combined.getPosition());

        r = Rule.createMustHaveAtMost('a', 2);
        combined = r.combine(Rule.createMustHaveAtMost('a', 1));
        assertEquals("a", combined.getCharactersAsString());
        assertEquals(1, combined.getAmount());
        assertEquals (-1, combined.getPosition());
    }
}
