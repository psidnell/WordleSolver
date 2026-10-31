package com.psidnell;

import com.psidnell.rules.CharacterFrequency;

import java.util.*;

public class Word implements Comparable<Word> {
    private final String word;
    private final int rank;

    public Word(String word) {
        this.word = word;
        rank = -1;
    }

    public Word(String word, int rank) {
        this.word = word;
        this.rank = rank;
    }

    public String getWord() {
        return word;
    }

    public int occurencesOf(Character c) {
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
            if (other.occurencesOf(c) > 0) {
                return true;
            }
        }
        return false;
    }

    public CharacterFrequency charactersInCommon(Word other) {
        CharacterFrequency result = new CharacterFrequency();
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            int occurrences = other.occurencesOf(c);
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
            if (other.occurencesOf(c) == 0 && !seen.contains(c)) {
                result.append(c);
                seen.add(c);
            }
        }
        return result.toString();
    }

    public WordComparison compareWords(Word other) {
        Boolean[] correctPositions = new Boolean[5];
        Boolean[] incorrectPositions = new Boolean[5];
        Boolean[] mismatches = new Boolean[5];

        for (int i = 0; i < 5; i++) {
            Character c1 = word.charAt(i);
            Character c2 = other.word.charAt(i);

            if (c1.equals(c2)) {
                correctPositions[i] = true;
                incorrectPositions[i] = false;
                mismatches[i] = false;
            } else if (other.occurencesOf(c1) > 0){
                correctPositions[i] = false;
                incorrectPositions[i] = true;
                mismatches[i] = false;
            } else {
                correctPositions[i] = false;
                incorrectPositions[i] = false;
                mismatches[i] = true;
            }
        }


        String charactersMissing = charactersMissing(other);
        CharacterFrequency commonFrequencies =  charactersInCommon(other);
        return new WordComparison(commonFrequencies, charactersMissing, equals(other), correctPositions, incorrectPositions, mismatches);
    }

    public Character charAt(int i) {
        return word.charAt(i);
    }

    @Override
    public int compareTo(Word o) {
        int result = Integer.compare(o.rank, rank);
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
}
