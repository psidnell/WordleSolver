package com.psidnell.rules;

import com.psidnell.Word;
import com.psidnell.WordComparison;

import java.util.*;

public class RuleSet {
    private final Set<Rule> rules = new TreeSet<>();

    public RuleSet() {
    }

    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        CharacterFrequency commonLettersRemaining = new CharacterFrequency(comparison.getCommonLetterFrequency());
        CharacterFrequency mustHaveAtLeast = new CharacterFrequency();
        // Handle basic character matches and misses
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                rules.add(Rule.createMatch(gc, i));
                mustHaveAtLeast.increase(gc);
                // We might have already consumed all the letters in a wrong place match
                if (commonLettersRemaining.occurrences(gc) > 0) {
                    commonLettersRemaining.decrease(gc);
                }
            } else if (comparison.getIncorrectPositions()[i]) {
                int remaining = commonLettersRemaining.occurrences(gc);
                if (remaining > 0) {
                    rules.add(Rule.createWrongPlace(gc, i));
                    mustHaveAtLeast.increase(gc);
                    commonLettersRemaining.decrease(gc);
                } else {
                    rules.add(Rule.createNoMatch(gc, i));
                }
            } else if (comparison.getMismatches()[i]) {
                rules.add(Rule.createMustNotHave(gc));
                rules.add(Rule.createNoMatch(gc, i));
            }
        }

        // Derive character occurrence minimums from rules we've already created
        mustHaveAtLeast.getFrequencies().forEach((ch, min) -> {
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

    public void addRuleSet(RuleSet other) {
        rules.addAll(other.rules);
    }
}
