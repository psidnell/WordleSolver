package com.psidnell.rules;

import com.psidnell.Word;
import com.psidnell.WordComparison;

import java.util.*;

public class RuleSet {
    private final Set<Rule> allRules = new TreeSet<>();
    //todo eliminate
    private final Rule[] oldRules = new Rule[5];

    //todo fold into rules
    private final Map<Character, Integer> mustHaveAtLeast = new TreeMap<>();
    private final Set<Character> mustNotHave = new TreeSet<>();


    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        Map<Character, Integer> commonLettersRemaining = new HashMap<>(comparison.getCommonLetterFrequency());

        // Must do correct positions first and knock out occurrences
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                oldRules[i] = new Rule(RuleType.MATCH, gc, i, -1);
                allRules.add(new Rule(RuleType.MATCH, gc, i, -1));
                commonLettersRemaining.put(gc, commonLettersRemaining.getOrDefault(gc, 0) - 1);
            }
        }

        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getIncorrectPositions()[i]) {
                Integer remaining = commonLettersRemaining.getOrDefault(gc, 0);
                if (remaining > 0) {
                    oldRules[i] = new Rule(RuleType.WRONG_PLACE, gc, i, -1);
                    allRules.add(new Rule(RuleType.WRONG_PLACE, gc, i, -1));
                    commonLettersRemaining.put(gc, remaining - 1);
                } else {
                    oldRules[i] = new Rule(RuleType.NO_MATCH, gc, i, -1);
                    allRules.add(new Rule(RuleType.NO_MATCH, gc, i, -1));
                }
            } else if (comparison.getMismatches()[i]) {
                allRules.add(new Rule(RuleType.MUST_NOT_HAVE, gc, -1, -1));
                mustNotHave.add(gc);
                oldRules[i] = new Rule(RuleType.NO_MATCH, gc, i, -1);
                allRules.add(new Rule(RuleType.NO_MATCH, gc, i, -1));
            }
        }

        // Now we can assign minimum values for some characters


        for (int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            switch(oldRules[i].getType()) {
                case MATCH:
                case WRONG_PLACE:
                    if (mustHaveAtLeast.containsKey(oldRules[i].getCharacter())) {
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

        for (Rule rule : allRules) {
            output.append(rule).append(" ");
        }

//        for (int i = 0; i < 5; i++) {
//            switch (oldRules[i].getType()) {
//                case MATCH:
//                    output.append(oldRules[i].getCharacter()).append("M").append(" ");
//                    break;
//                case WRONG_PLACE:
//                    output.append(oldRules[i].getCharacter()).append("?").append(" ");
//                    break;
//                default: // NO_MATCH
//                    output.append(oldRules[i].getCharacter()).append("X").append(" ");
//                    break;
//            }
//        }

        mustHaveAtLeast.keySet().forEach(c
                -> output.append(c).append(">=").append(mustHaveAtLeast.get(c)).append(" "));

//        mustNotHave.forEach(c
//                -> output.append("!").append(c).append(" "));

        return output.toString().trim().toUpperCase();
    }

    public boolean match(Word word) {

        for (Rule rule : allRules) {
            if (!rule.match(word)) {
                return false;
            }
        }

        for (Character c : mustHaveAtLeast.keySet() ) {
            // Does the word have enough of the letters we guessed?
            int atLeast = mustHaveAtLeast.get(c);
            int actual = word.occurencesOf(c);
            if (actual < atLeast) {
                return false;
            }
        }

        return true;
    }
}
