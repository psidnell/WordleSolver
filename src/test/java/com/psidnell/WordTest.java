package com.psidnell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WordTest
{
    @Test
    public void testOccurrencesOfSingle() {
        String str = "abcde";
        Word word = new Word(str);
        assertEquals(str, word.getWord());
        assertEquals(1, word.occurencesOf(word.getWord().charAt(0)));
        assertEquals(1, word.occurencesOf(word.getWord().charAt(1)));
        assertEquals(1, word.occurencesOf(word.getWord().charAt(2)));
        assertEquals(1, word.occurencesOf(word.getWord().charAt(3)));
        assertEquals(1, word.occurencesOf(word.getWord().charAt(4)));
    }

    @Test
    public void testOccurrencesOfMultiple() {
        String str = "aaaaa";
        Word word = new Word(str);
        assertEquals(str, word.getWord());
        assertEquals(5, word.occurencesOf(word.getWord().charAt(0)));
    }

    @Test
    public void testHaveCharactersInCommon() {
        Word word1 = new Word("abcde");
        Word word2 = new Word("fghij");
        Word word3 = new Word("axxxf");
        assertFalse(word1.haveCommonCharacters(word2));
        assertTrue(word1.haveCommonCharacters(word3));
        assertTrue(word2.haveCommonCharacters(word3));
    }

    @Test
    public void testCharactersInCommon() {
        Word word1 = new Word("abcde");
        Word word2 = new Word("defghi");
        WordComparison wordComparison = word1.compareWords(word2);
        assertEquals(2, wordComparison.getCommonLetterFrequency().getFrequencies().size());
        assertEquals(1, wordComparison.getCommonLetterFrequency().getFrequencies().get((char) 'd'));

        word1 = new Word("aaxxx");
        word2 = new Word("zzaazz");
        wordComparison = word1.compareWords(word2);
        assertEquals(1, wordComparison.getCommonLetterFrequency().getFrequencies().size());
        assertEquals(2, wordComparison.getCommonLetterFrequency().getFrequencies().get((char) 'a'));
    }

    @Test
    public void testLettersMissing() {
        Word word1 = new Word("abcde");
        Word word2 = new Word("defghi");
        WordComparison wordComparison = word1.compareWords(word2);
        assertEquals("abc", wordComparison.getLettersMissing());
    }

    @Test
    public void testFullMatch() {
        assertFalse(new Word("abcde").compareWords(new Word("abcdx")).isFullMatch());
        assertTrue(new Word("abcde").compareWords(new Word("abcde")).isFullMatch());
    }
}
