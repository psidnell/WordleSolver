package com.psidnell;

import java.util.HashMap;
import java.util.Map;

public class Match {
    private MatchType matchTypes[] = new MatchType[5];
    private Word guess;

    public enum MatchType {
        MATCH,
        NO_MATCH,
        WRONG_PLACE
    }

    public Match(Word guess, Word solution) {
        this.guess = guess;
        String s = solution.getWord();
        String g = guess.getWord();

        Map<Character, Integer> charGuessesRemaining = new HashMap<>();
        for (char c : g.toCharArray()) {
            Character character = c;
            charGuessesRemaining.put(c, solution.occurencesOf(character));
        }

        // First look for direct matches
        for (int i = 0; i < 5; i++) {
            Character gc = g.charAt(i);
            Character sc = s.charAt(i);
            int remaining = charGuessesRemaining.get(gc);
            if (gc.equals(sc)) {
                matchTypes[i] = MatchType.MATCH;
                charGuessesRemaining.put(gc, remaining - 1);
            }
        }

        // Then for different kinds of miss
        for (int i = 0; i < 5; i++) {
            Character gc = g.charAt(i);
            Character sc = s.charAt(i);
            int remaining = charGuessesRemaining.get(gc);
            if (gc.equals(sc)) {
                // Handled
            } else if (remaining > 0) {
                matchTypes[i] = MatchType.WRONG_PLACE;
                charGuessesRemaining.put(gc, remaining - 1);
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
                default:
                    output.append("!" + guess.getWord().charAt(i) + " ");
                    break;
            }
        }
        return output.toString().trim().toUpperCase();
    }
}
