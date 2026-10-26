package com.psidnell;

import java.util.HashMap;
import java.util.Map;

public class Match {
    private MatchType matchTypes[] = new MatchType[5];
    private Map<Character, Integer> mustHaveAtLeast = new HashMap<>();
    private Word guess;
    private boolean match;

    public enum MatchType {
        MATCH,
        NO_MATCH,
        WRONG_PLACE
    }

    public Match(Word guess, Word solution) {
        this.guess = guess;
        String s = solution.getWord();
        String g = guess.getWord();
        match = s.endsWith(g);

        Map<Character, Integer> charGuessesRemaining = new HashMap<>();
        for (char c : g.toCharArray()) {
            Character character = c;
            charGuessesRemaining.put(c, solution.occurencesOf(character));
            mustHaveAtLeast.put(character, 0);
        }

        // First look for direct matches
        for (int i = 0; i < 5; i++) {
            Character gc = g.charAt(i);
            Character sc = s.charAt(i);
            int remaining = charGuessesRemaining.get(gc);
            int mustHaves = mustHaveAtLeast.get(gc);
            if (gc.equals(sc)) {
                matchTypes[i] = MatchType.MATCH;
                charGuessesRemaining.put(gc, remaining - 1);
                mustHaveAtLeast.put(gc, mustHaves + 1);
            }
        }

        // Then for different kinds of miss
        for (int i = 0; i < 5; i++) {
            Character gc = g.charAt(i);
            Character sc = s.charAt(i);
            int remaining = charGuessesRemaining.get(gc);
            int mustHaves = mustHaveAtLeast.get(gc);
            if (gc.equals(sc)) {
                // Handled
            } else if (remaining > 0) {
                matchTypes[i] = MatchType.WRONG_PLACE;
                charGuessesRemaining.put(gc, remaining - 1);
                mustHaveAtLeast.put(gc, mustHaves + 1);
            } else {
                matchTypes[i] = MatchType.NO_MATCH;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            switch (matchTypes[i]) {
                case MATCH:
                    output.append(" " + guess.getWord().charAt(i) + " ");
                    break;
                case WRONG_PLACE:
                    output.append("?" + guess.getWord().charAt(i)+ " ");
                    break;
                default: // NO_MATCH
                    output.append("!" + guess.getWord().charAt(i) + " ");
                    break;
            }
        }

        mustHaveAtLeast.keySet().forEach(c
                -> output.append(" ").append(c).append(">=").append(mustHaveAtLeast.get(c)));

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

        return false;
    }

    public boolean isMatch() {
        return match;
    }
}
