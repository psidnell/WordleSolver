package com.psidnell.words;

import com.psidnell.PlayerException;
import com.psidnell.rules.CharacterFrequency;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Word implements Comparable<Word> {
    private CharacterFrequency frequencies = null;
    private final String word;
    private final int rank;

    public Word(String word) {
        if (word.length() != 5) {
            throw new PlayerException("Word must be 5 characters long");
        }
        this.word = word.toUpperCase();
        rank = -1;
    }

    public Word(String word, int rank) {
        if (word.length() != 5) {
            throw new PlayerException("Word must be 5 characters long");
        }
        this.word = word.toUpperCase();
        this.rank = rank;
    }

    public String getWord() {
        return word;
    }

    public int occurrencesOf(Character c) {
        int occurrences = 0;
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == c) {
                occurrences++;
            }
        }
        return occurrences;
    }

    @Override
    public String toString() {
        return word;
    }

    public boolean haveCommonCharacters(Word other) {
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            if (other.occurrencesOf(c) > 0) {
                return true;
            }
        }
        return false;
    }

    public CharacterFrequency charactersInCommon(Word other) {
        CharacterFrequency result = new CharacterFrequency();
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            int occurrences = other.occurrencesOf(c);
            if (occurrences > 0) {
                result.set(c, occurrences);
            }
        }
        return result;
    }

    public String charactersMissing(Word other) {
        StringBuilder result = new StringBuilder();
        Set<Character> seen = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            if (other.occurrencesOf(c) == 0 && !seen.contains(c)) {
                result.append(c);
                seen.add(c);
            }
        }
        return result.toString();
    }

    public WordComparison compareWords(Word other) {
        return new WordComparison(this, other);
    }

    public Character charAt(int i) {
        return word.charAt(i);
    }

    @Override
    public int compareTo(Word o) {
        int result = Integer.compare(rank, o.rank);
        if (result != 0) {
            return result;
        }

        return word.compareTo(o.word);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return rank == word1.rank && Objects.equals(word, word1.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, rank);
    }

    public int getRank() {
        return rank;
    }

    public CharacterFrequency geFrequencies() {
        if (frequencies == null) {
            frequencies = new CharacterFrequency(this);
        }
        return this.frequencies;
    }
}
