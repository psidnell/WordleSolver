package com.psidnell;

import java.util.*;

public class Match {
    private MatchType matchTypes[] = new MatchType[5];
    private Map<Character, Integer> mustHaveAtLeast = new TreeMap<>();
    private Set<Character> misses = new TreeSet<>();
    private Word guess;
    private boolean match;

    public enum MatchType {
        MATCH,
        NO_MATCH,
        WRONG_PLACE
    }

    public Match(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);

        Map<Character, Integer> commonLetterFrequencies = comparison.getCommonLetterFrequency();


        this.guess = guess;
        String s = solution.getWord();
        String g = guess.getWord();
        match = s.equals(g);



        // Build a count for each characters occurences
        // wo we can decrement as we find positional matches
        Map<Character, Integer> occurrencesForCharactersRemaining = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            Character gc = g.charAt(i);
            int occurrences = solution.occurencesOf(gc);
            occurrencesForCharactersRemaining.put(gc, occurrences);
        }

        for (int i = 0; i < 5; i++) {
            Character gc = g.charAt(i);
            Character sc = s.charAt(i);
            int occurrencesRemaining = occurrencesForCharactersRemaining.get(gc);
            if (gc.equals(sc)) {
                // Characters match
                matchTypes[i] = MatchType.MATCH;
                mustHaveAtLeast.put(gc, commonLetterFrequencies.get(sc));
                // We have consumed one of these characters
                occurrencesForCharactersRemaining.put(gc, occurrencesRemaining - 1);
            } else if (occurrencesRemaining > 0) {
                // Character in wrong position
                if (!mustHaveAtLeast.containsKey(gc)) {
                    // We've found an additional unconsumed occurrence
                    matchTypes[i] = MatchType.WRONG_PLACE;
                    mustHaveAtLeast.put(gc, occurrencesRemaining);
                    occurrencesForCharactersRemaining.put(gc, occurrencesRemaining - 1);
                } else {
                    // TODO must have at most???
                    matchTypes[i] = MatchType.WRONG_PLACE;
                    occurrencesForCharactersRemaining.put(gc, occurrencesRemaining - 1);
                }
            } else {
                // No match or consumed occurrences
                matchTypes[i] = MatchType.NO_MATCH;
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

    public Word getGuess() {
        return guess;
    }
}
