package com.psidnell.ruleset;

import com.psidnell.rules.CharacterFrequency;
import com.psidnell.words.Word;
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
                Arguments.of("ABABA", "{A:3,B:2}")
        );
    }
    @ParameterizedTest
    @MethodSource("compareData")
    public void testConstruction(String word, String toString) {
        assertEquals(toString, new CharacterFrequency(new Word(word)).toString());
    }
}
