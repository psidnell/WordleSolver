package com.psidnell.ruleset.util;

import com.psidnell.util.CharacterFrequency;
import com.psidnell.words.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterFrequencyTest {


    public static Stream<Arguments> compareData() {
        return Stream.of(
                // Word1, toString
                Arguments.of("AAAAA", "{A:5}"),
                Arguments.of("AAAAB", "{A:4,B:1}"),
                Arguments.of("ABABA", "{A:3,B:2}"),
                Arguments.of("BABAB", "{A:2,B:3}")
        );
    }
    @ParameterizedTest
    @MethodSource("compareData")
    public void testConstruction(String word, String toString) {
        CharacterFrequency f1 = new CharacterFrequency(new Word(word));
        assertEquals(toString, f1.toString());
        CharacterFrequency f2 = new CharacterFrequency(f1);
        assertEquals(toString, f2.toString());
    }

    @Test
    public void testIncrease() {
        CharacterFrequency f = new CharacterFrequency();
        assertEquals("{}", f.toString());
        assertEquals(0, f.occurrences('A'));
        f.increase('A');
        assertEquals("{A:1}", f.toString());
        assertEquals(1, f.occurrences('A'));
        f.increase('A');
        assertEquals("{A:2}", f.toString());
        assertEquals(2, f.occurrences('A'));
    }

    @Test
    public void testDecrease() {
        CharacterFrequency f = new CharacterFrequency(new Word("BBBAA"));
        assertEquals("{A:2,B:3}", f.toString());
        assertEquals(2, f.occurrences('A'));
        f.decrease('A');
        assertEquals("{A:1,B:3}", f.toString());
        assertEquals(1, f.occurrences('A'));
        f.decrease('A');
        assertEquals("{B:3}", f.toString());
        assertEquals(0, f.occurrences('A'));
    }
}
