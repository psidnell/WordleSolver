package com.psidnell.words;

import com.psidnell.ruleset.CharacterFrequency;

public class WordComparison {
    // todo How much of all this am I using?
    private final CharacterFrequency commonLetterFrequency;
    private final String lettersMissing;
    private final boolean fullMatch;
    private final Boolean[] correctPositions;
    private final Boolean[] incorrectPositions;
    private final Boolean[] mismatches;
    private final int charactersInCommon;


    public WordComparison(Word w1, Word w2) {
        Boolean[] correctPositions = new Boolean[5];
        Boolean[] incorrectPositions = new Boolean[5];
        Boolean[] mismatches = new Boolean[5];

        for (int i = 0; i < 5; i++) {
            Character c1 = w1.charAt(i);
            Character c2 = w2.charAt(i);

            if (c1.equals(c2)) {
                correctPositions[i] = true;
                incorrectPositions[i] = false;
                mismatches[i] = false;
            } else if (w2.occurrencesOf(c1) > 0) {
                correctPositions[i] = false;
                incorrectPositions[i] = true;
                mismatches[i] = false;
            } else {
                correctPositions[i] = false;
                incorrectPositions[i] = false;
                mismatches[i] = true;
            }
        }

        String charactersMissing = w1.charactersMissing(w2);
        CharacterFrequency commonFrequencies = w1.charactersInCommon(w2);
        int charactersInCommon1 = w1.charactersInCommon(w2).overlapSize();
        this.commonLetterFrequency = commonFrequencies;
        this.lettersMissing = charactersMissing;
        this.fullMatch = w1.equals(w2);
        this.correctPositions = correctPositions;
        this.incorrectPositions = incorrectPositions;
        this.mismatches = mismatches;
        this.charactersInCommon = charactersInCommon1;
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
        return
                10000 * trueCount(correctPositions) +
                1000 * charactersInCommon +
                100 * lettersMissing.length() +
                10 * trueCount(incorrectPositions) +
                trueCount(mismatches);


    }

    private int trueCount(Boolean[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i]) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Correct{");
        for (int i = 0; i < 5; i++) {
            sb.append(correctPositions[i] ? 'Y' : '_');
        }

        sb.append("} Incorrect{");
        for (int i = 0; i < 5; i++) {
            sb.append(incorrectPositions[i] ? 'Y' : '_');
        }

        sb.append("} Mismatches{");
        for (int i = 0; i < 5; i++) {
            sb.append(mismatches[i] ? 'Y' : '_');
        }
        sb.append("} Missing{").append(lettersMissing);

        sb.append("} Freq").append(commonLetterFrequency);

        sb.append(" Cmn:").append(charactersInCommon);

        sb.append(" Cmp:").append(fullMatch);

        sb.append(" Mis{").append(lettersMissing);

        sb.append("} Rnk:").append(getRank());
        return sb.toString();
    }
}
