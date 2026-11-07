package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals(match, new MatchRule(c, pos).match(new Word(word)));
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
        assertEquals(match, new NotRule(c, pos).match(new Word(word)));
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
        assertEquals(match, new WrongPlaceRule(c, pos).match(new Word(word)));
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
        assertEquals(match, new MustNotHaveRule(c).match(new Word(word)));
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
        assertEquals(match, new MustHaveAtLeastRule(c, count).match(new Word(word)));
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
        assertEquals(match, new MustHaveAtMostRule(c, count).match(new Word(word)));
    }

    @Test()
    void testCombineMatch() {
        MatchRule r = new MatchRule('A', 3);
        MatchRule combined = (MatchRule) r.combine(new MatchRule('A', 3));
        assertEquals('A', combined.getCharacter());
        assertEquals (3, combined.getPosition());
    }

    void testCombineMatchWithDifferentChars() {
        MatchRule r = new MatchRule('A', 3);
        MatchRule combined = (MatchRule) r.combine(new NotRule('B', 3));
        assertEquals('A', combined.getCharacter());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchWithDifferentChars() {
        NotRule r = new NotRule('A', 3);
        NotRule combined = (NotRule) r.combine(new NotRule('B', 3));
        assertEquals("AB", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchWithSameChars() {
        NotRule r = new NotRule('A', 3);
        NotRule combined = (NotRule) r.combine(new NotRule('A', 3));
        assertEquals("A", combined.getCharactersAsString());
        assertEquals (3, combined.getPosition());
    }

    @Test()
    void testCombineNoMatchMismatchedPositions() {
        Rule r = new NotRule('A', 3);
        assertThrows(IllegalArgumentException.class, () -> r.combine(new NotRule('B', 4)));
    }

    @Test()
    void testCombineMustNotHave() {
        MustNotHaveRule r = new MustNotHaveRule('A');
        MustNotHaveRule combined = (MustNotHaveRule) r.combine(new MustNotHaveRule('B'));
        assertEquals("AB", combined.getCharacters().toString());
    }

    @Test()
    void testCombineMustHaveAtLeast() {
        MustHaveAtLeastRule r = new MustHaveAtLeastRule('A', 1);
        MustHaveAtLeastRule combined = (MustHaveAtLeastRule) r.combine(new MustHaveAtLeastRule('A', 2));
        assertEquals('A', combined.getCharacter());
        assertEquals(2, combined.getAmount());
        assertEquals (-1, combined.getPosition());

        r = new MustHaveAtLeastRule('A', 2);
        combined = (MustHaveAtLeastRule) r.combine(new MustHaveAtLeastRule('A', 1));
        assertEquals('A', combined.getCharacter());
        assertEquals(2, combined.getAmount());
        assertEquals (-1, combined.getPosition());
    }

    @Test()
    void testCombineMustHaveAtMost() {
        MustHaveAtMostRule r = new MustHaveAtMostRule('A', 1);
        MustHaveAtMostRule combined = (MustHaveAtMostRule) r.combine(new MustHaveAtMostRule('A', 2));
        assertEquals('A', combined.getCharacter());
        assertEquals(1, combined.getAmount());

        r = new MustHaveAtMostRule('A', 2);
        combined = (MustHaveAtMostRule) r.combine(new MustHaveAtMostRule('A', 1));
        assertEquals('A', combined.getCharacter());
        assertEquals(1, combined.getAmount());
    }
}
