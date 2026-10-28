package com.psidnell.rules;

import com.psidnell.Word;
import com.psidnell.WordComparison;

import java.util.*;

public class RuleSet {
    private final Rule matchTypes[] = new Rule[5];
    private final Map<Character, Integer> mustHaveAtLeast = new TreeMap<>();
    private final Set<Character> mustNotHave = new TreeSet<>();
    private final boolean match;

    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        Map<Character, Integer> commonLettersRemaining = new HashMap<>(comparison.getCommonLetterFrequency());
        match = comparison.isFullMatch();

        // Must do correct positions first and knock out occurrences
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                matchTypes[i] = new Rule(RuleType.MATCH, gc, i);
                commonLettersRemaining.put(gc, commonLettersRemaining.getOrDefault(gc, 0) - 1);
            }
        }

        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getIncorrectPositions()[i]) {
                Integer remaining = commonLettersRemaining.getOrDefault(gc, 0);
                if (remaining > 0) {
                    matchTypes[i] =  new Rule(RuleType.WRONG_PLACE, gc, i);
                    commonLettersRemaining.put(gc, remaining - 1);
                } else {
                    matchTypes[i] = new Rule(RuleType.NO_MATCH, gc, i);
                }
            } else if (comparison.getMismatches()[i]) {
                mustNotHave.add(gc);
                matchTypes[i] =  new Rule(RuleType.NO_MATCH, gc, i);
            }
        }

        // Now we can assign minimum values for some characters
        for (int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            switch(matchTypes[i].getType()) {
                case MATCH:
                case WRONG_PLACE:
                    if (mustHaveAtLeast.containsKey(matchTypes[i].getCharacter())) {
                        mustHaveAtLeast.put(gc, mustHaveAtLeast.get(gc) + 1);
                    } else {
                        mustHaveAtLeast.put(gc, 1);
                    }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(" ");
        for (int i = 0; i < 5; i++) {
            switch (matchTypes[i].getType()) {
                case MATCH:
                    output.append(matchTypes[i].getCharacter()).append("M").append(" ");
                    break;
                case WRONG_PLACE:
                    output.append(matchTypes[i].getCharacter()).append("?").append(" ");
                    break;
                default: // NO_MATCH
                    output.append(matchTypes[i].getCharacter()).append("X").append(" ");
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
            Character wordLetter = word.getWord().charAt(i);
            switch (matchTypes[i].getType()) {
                case MATCH:
                    // If we guessed a letter correctly and the candidate
                    // word does not have same letter in same place then it's
                    // not a match
                    if (!matchTypes[i].getCharacter().equals(wordLetter)) {
                        return true;
                    }
                    break;
                case NO_MATCH:
                    // Must NOT have the letter in this position
                    if (matchTypes[i].getCharacter().equals(wordLetter)) {
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
}
