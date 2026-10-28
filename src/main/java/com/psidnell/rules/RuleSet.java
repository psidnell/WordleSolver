package com.psidnell.rules;

import com.psidnell.Word;
import com.psidnell.WordComparison;

import java.util.*;

public class RuleSet {
    private final Set<Rule> rules = new TreeSet<>();

    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        Map<Character, Integer> commonLettersRemaining = new HashMap<>(comparison.getCommonLetterFrequency());

        // Handle basic character matches and misses
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                rules.add(Rule.createMatch(gc, i));
                commonLettersRemaining.put(gc, commonLettersRemaining.getOrDefault(gc, 0) - 1);
            } else if (comparison.getIncorrectPositions()[i]) {
                Integer remaining = commonLettersRemaining.getOrDefault(gc, 0);
                if (remaining > 0) {
                    rules.add(Rule.createWrongPlace(gc, i));
                    commonLettersRemaining.put(gc, remaining - 1);
                } else {
                    rules.add(Rule.createNoMatch(gc, i));
                }
            } else if (comparison.getMismatches()[i]) {
                rules.add(Rule.createMustNotHave(gc));
                rules.add(Rule.createNoMatch(gc, i));
            }
        }

        // Derive character occurrence minimums from rules we've already created
        // TODO can I fold this into the loop above?
        Map<Character, Integer> mustHaveAtLeast = new TreeMap<>();
        rules.forEach((rule) -> {
            switch(rule.getType()) {
                case MATCH:
                case WRONG_PLACE:
                    Character gc = guess.charAt(rule.getPosition());
                    if (mustHaveAtLeast.containsKey(rule.getCharacter())) {
                        mustHaveAtLeast.put(gc, mustHaveAtLeast.get(gc) + 1);
                    } else {
                        mustHaveAtLeast.put(gc, 1);
                    }
            }
        });
        mustHaveAtLeast.forEach((ch, min) -> {
            rules.add(Rule.createMustHaveAtLeast(ch, min));
        });
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (Rule rule : rules) {
            output.append(rule).append(" ");
        }

        return output.toString().trim();
    }

    public boolean match(Word word) {

        for (Rule rule : rules) {
            if (!rule.match(word)) {
                return false;
            }
        }

        return true;
    }
}
