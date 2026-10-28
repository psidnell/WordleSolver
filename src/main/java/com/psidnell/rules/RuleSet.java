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
                rules.add(new Rule(RuleType.MATCH, gc, i, -1));
                commonLettersRemaining.put(gc, commonLettersRemaining.getOrDefault(gc, 0) - 1);
            } else if (comparison.getIncorrectPositions()[i]) {
                Integer remaining = commonLettersRemaining.getOrDefault(gc, 0);
                if (remaining > 0) {
                    rules.add(new Rule(RuleType.WRONG_PLACE, gc, i, -1));
                    commonLettersRemaining.put(gc, remaining - 1);
                } else {
                    rules.add(new Rule(RuleType.NO_MATCH, gc, i, -1));
                }
            } else if (comparison.getMismatches()[i]) {
                rules.add(new Rule(RuleType.MUST_NOT_HAVE, gc, -1, -1));
                rules.add(new Rule(RuleType.NO_MATCH, gc, i, -1));
            }
        }

        // Derive character occurrence minimums from rules we've already created
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
            rules.add(new Rule(RuleType.MUST_HAVE_AT_LEAST, ch, -1, min));
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
