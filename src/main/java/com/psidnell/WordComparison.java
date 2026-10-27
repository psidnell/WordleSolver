package com.psidnell;

import java.util.HashMap;
import java.util.Map;

public class WordComparison {
    private final Map<Character, Integer> commonLetterFrequency;
    private final String lettersMissing;
    private final boolean fullMatch;
    private final Boolean[] correctPositions;
    private final Boolean[] incorrectPositions;
    private final Boolean[] mismatches;

    public WordComparison(Map<Character, Integer> commonLetterFrequency, String lettersMissing, boolean fullMatch,
                          Boolean[] correctPositions, Boolean[] incorrectPositions, Boolean[] mismatches) {
        this.commonLetterFrequency = commonLetterFrequency;
        this.lettersMissing = lettersMissing;
        this.fullMatch = fullMatch;
        this.correctPositions = correctPositions;
        this.incorrectPositions = incorrectPositions;
        this.mismatches = mismatches;
    }

    public Map<Character, Integer> getCommonLetterFrequency() {
        return commonLetterFrequency;
    }

    public String getLettersMissing() {
        return lettersMissing;
    }

    public boolean isFullMatch() {
        return fullMatch;
    }
}
