package com.psidnell.words;

import com.psidnell.rules.CharacterFrequency;

public class WordComparison {
    private final CharacterFrequency commonLetterFrequency;
    private final String lettersMissing;
    private final boolean fullMatch;
    private final Boolean[] correctPositions;
    private final Boolean[] incorrectPositions;
    private final Boolean[] mismatches;
    private final int charactersInCommon;

    public WordComparison(CharacterFrequency commonLetterFrequency, String lettersMissing, boolean fullMatch,
                          Boolean[] correctPositions, Boolean[] incorrectPositions, Boolean[] mismatches, int charactersInCommon) {
        this.commonLetterFrequency = commonLetterFrequency;
        this.lettersMissing = lettersMissing;
        this.fullMatch = fullMatch;
        this.correctPositions = correctPositions;
        this.incorrectPositions = incorrectPositions;
        this.mismatches = mismatches;
        this.charactersInCommon = charactersInCommon;
    }

    public CharacterFrequency getCommonLetterFrequency() {
        return commonLetterFrequency;
    }

    public String getLettersMissing() {
        return lettersMissing;
    }

    public boolean isFullMatch() {
        return fullMatch;
    }

    public Boolean[] getCorrectPositions() {
        return correctPositions;
    }

    public Boolean[] getIncorrectPositions() {
        return incorrectPositions;
    }

    public Boolean[] getMismatches() {
        return mismatches;
    }

    public int charactersInCommon() {
        return charactersInCommon;
    }

    public int getRank() {
        return 100 * nonNullCount(correctPositions) +
                10 * nonNullCount(incorrectPositions) +
                nonNullCount(mismatches);
    }

    private int nonNullCount(Object[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                count++;
            }
        }
        return count;
    }
}
