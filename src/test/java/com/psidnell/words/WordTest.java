package com.psidnell.words;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class WordTest
{
    @Test
    public void testOccurrencesOfSingle() {
        String str = "ABCDE";
        Word word = new Word(str);
        assertEquals(str, word.getWord());
        assertEquals(1, word.occurrencesOf(word.getWord().charAt(0)));
        assertEquals(1, word.occurrencesOf(word.getWord().charAt(1)));
        assertEquals(1, word.occurrencesOf(word.getWord().charAt(2)));
        assertEquals(1, word.occurrencesOf(word.getWord().charAt(3)));
        assertEquals(1, word.occurrencesOf(word.getWord().charAt(4)));
    }

    @Test
    public void testOccurrencesOfMultiple() {
        String str = "AAAAA";
        Word word = new Word(str);
        assertEquals(str, word.getWord());
        assertEquals(5, word.occurrencesOf(word.getWord().charAt(0)));
    }

    @Test
    public void testHaveCharactersInCommon() {
        Word word1 = new Word("ABCDE");
        Word word2 = new Word("FGHIJ");
        Word word3 = new Word("AXXXF");
        assertFalse(word1.haveCommonCharacters(word2));
        assertTrue(word1.haveCommonCharacters(word3));
        assertTrue(word2.haveCommonCharacters(word3));
    }

    @Test
    public void testCharactersInCommon() {
        Word word1 = new Word("ABCDE");
        Word word2 = new Word("DEFGH");
        WordComparison wordComparison = word1.compareWords(word2);
        assertEquals(2, wordComparison.getCommonLetterFrequency().frequencies().size());
        assertEquals(1, wordComparison.getCommonLetterFrequency().frequencies().get('D'));

        word1 = new Word("AAXXX");
        word2 = new Word("ZZAAZ");
        wordComparison = word1.compareWords(word2);
        assertEquals(1, wordComparison.getCommonLetterFrequency().frequencies().size());
        assertEquals(2, wordComparison.getCommonLetterFrequency().frequencies().get('A'));
    }

    @Test
    public void testLettersMissing() {
        Word word1 = new Word("ABCDE");
        Word word2 = new Word("DEFGH");
        WordComparison wordComparison = word1.compareWords(word2);
        assertEquals("ABC", wordComparison.getLettersMissing());
    }

    @Test
    public void testFullMatch() {
        assertFalse(new Word("ABCDE").compareWords(new Word("ABCDX")).isFullMatch());
        assertTrue(new Word("ABCDE").compareWords(new Word("ABCDE")).isFullMatch());
    }

    public static Stream<Arguments> compareWordsData() {
        return Stream.of(
                // Word1, Word2, toString
                Arguments.of("AAAAA", "BBBBB", "Correct{_____} Incorrect{_____} Mismatches{YYYYY} Missing{A} Freq{} Cmn:0 Cmp:false Mis{A} Rnk:5010"),
                Arguments.of("AAAAA", "AAAAA", "Correct{YYYYY} Incorrect{_____} Mismatches{_____} Missing{} Freq{A:5} Cmn:5 Cmp:true Mis{} Rnk:50005"),
                Arguments.of("AAAAA", "AABBB", "Correct{YY___} Incorrect{__YYY} Mismatches{_____} Missing{} Freq{A:2} Cmn:2 Cmp:false Mis{} Rnk:20302"),
                Arguments.of("AAABB", "BBAAA", "Correct{__Y__} Incorrect{YY_YY} Mismatches{_____} Missing{} Freq{A:3,B:2} Cmn:5 Cmp:false Mis{} Rnk:10405"),
                Arguments.of("ABCDE", "BCXXX", "Correct{_____} Incorrect{_YY__} Mismatches{Y__YY} Missing{ADE} Freq{B:1,C:1} Cmn:2 Cmp:false Mis{ADE} Rnk:3232")
        );
    }
    @ParameterizedTest
    @MethodSource("compareWordsData")
    public void testCompareWords(String word1, String word2, String toString) {
        assertEquals(toString, new Word(word1).compareWords(new Word(word2)).toString());
    }

}
