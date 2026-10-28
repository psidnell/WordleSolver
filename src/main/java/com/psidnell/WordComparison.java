package com.psidnell;

import com.psidnell.rules.CharacterFrequency;

import java.util.HashMap;
import java.util.Map;

public class WordComparison {
    private final CharacterFrequency commonLetterFrequency;
    private final String lettersMissing;
    private final boolean fullMatch;
    private final Boolean[] correctPositions;
    private final Boolean[] incorrectPositions;
    private final Boolean[] mismatches;

    public WordComparison(CharacterFrequency commonLetterFrequency, String lettersMissing, boolean fullMatch,
                          Boolean[] correctPositions, Boolean[] incorrectPositions, Boolean[] mismatches) {
        this.commonLetterFrequency = commonLetterFrequency;
        this.lettersMissing = lettersMissing;
        this.fullMatch = fullMatch;
        this.correctPositions = correctPositions;
        this.incorrectPositions = incorrectPositions;
        this.mismatches = mismatches;
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
}
