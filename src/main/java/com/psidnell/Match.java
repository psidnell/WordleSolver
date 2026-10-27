package com.psidnell;

import java.util.*;

public class Match {
    private final MatchType matchTypes[] = new MatchType[5];
    private final Map<Character, Integer> mustHaveAtLeast = new TreeMap<>();
    private final Set<Character> mustNotHave = new TreeSet<>();
    private final Word guess;
    private final boolean match;

    public enum MatchType {
        MATCH,
        NO_MATCH,
        WRONG_PLACE
    }

    public Match(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        Map<Character, Integer> commonLettersRemaining = new HashMap<>(comparison.getCommonLetterFrequency());
        this.guess = guess;
        match = comparison.isFullMatch();

        // Must do correct positions first and knock out occurrences
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                matchTypes[i] = MatchType.MATCH;
                commonLettersRemaining.put(gc, commonLettersRemaining.getOrDefault(gc, 0) - 1);
            }
        }

        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getIncorrectPositions()[i]) {
                Integer remaining = commonLettersRemaining.getOrDefault(gc, 0);
                if (remaining > 0) {
                    matchTypes[i] = MatchType.WRONG_PLACE;
                    commonLettersRemaining.put(gc, remaining - 1);
                } else {
                    matchTypes[i] = MatchType.NO_MATCH;
                }
            } else if (comparison.getMismatches()[i]) {
                mustNotHave.add(gc);
                matchTypes[i] = MatchType.NO_MATCH;
            }
        }

        // Now we can assign minimum values for some characters
        for (int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            switch(matchTypes[i]) {
                case MATCH:
                case WRONG_PLACE:
                    if (mustHaveAtLeast.containsKey(gc)) {
                        mustHaveAtLeast.put(gc, mustHaveAtLeast.get(gc) + 1);
                    } else {
                        mustHaveAtLeast.put(gc, 1);
                    }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(guess.getWord());
        output.append(" ");
        for (int i = 0; i < 5; i++) {
            char c = guess.getWord().charAt(i);
            switch (matchTypes[i]) {
                case MATCH:
                    output.append(c).append("M").append(" ");
                    break;
                case WRONG_PLACE:
                    output.append(c).append("?").append(" ");
                    break;
                default: // NO_MATCH
                    output.append(c).append("X").append(" ");
                    break;
            }
        }

        mustHaveAtLeast.keySet().forEach(c
                -> output.append(c).append(">=").append(mustHaveAtLeast.get(c)).append(" "));

        mustNotHave.forEach(c
                -> output.append("!").append(c).append(" "));

        return output.toString().trim().toUpperCase();
    }

    public boolean wouldEliminateWord(Word word) {
        for (int i = 0; i < 5; i++) {
            Character guessLetter = guess.getWord().charAt(i);
            Character wordLetter = word.getWord().charAt(i);
            switch (matchTypes[i]) {
                case MATCH:
                    // If we guessed a letter correctly and the candidate
                    // word does not have same letter in same place then it's
                    // not a match
                    if (!guessLetter.equals(wordLetter)) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }

        for (Character c : mustHaveAtLeast.keySet() ) {
            // Does the word have enough of the letters we guessed?
            int atLeast = mustHaveAtLeast.get(c);
            int actual = word.occurencesOf(c);
            if (actual < atLeast) {
                return true;
            }
        }

        for (Character c : mustNotHave ) {
            // Does the have letters we've eliminated?
            int count = word.occurencesOf(c);
            if (count > 0) {
                return true;
            }
        }

        return false;
    }

    public boolean isMatch() {
        return match;
    }

    public Word getGuess() {
        return guess;
    }
}
