package com.psidnell.rules;

import com.psidnell.Word;
import com.psidnell.WordComparison;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RuleSet {

    private Rule[] matches = new Rule[5];
    private Rule[] wrongPlaces = new Rule[5];
    private Rule[] noMatches = new Rule[5];;
    private Rule mustNotHave = null;
    private Map<Character, Rule> mustHaveAtLeast = new TreeMap<>();

    // todo Necessary?
    private final Set<Rule> allRules = new TreeSet<>();

    public RuleSet() {
    }

    public RuleSet(RuleSet other) {
        other.allRules.forEach(this::addRule);
    }

    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        CharacterFrequency commonLettersRemaining = new CharacterFrequency(comparison.getCommonLetterFrequency());
        CharacterFrequency mustHaveAtLeast = new CharacterFrequency();
        // Handle basic character matches and misses
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                addRule(Rule.createMatch(gc, i));
                mustHaveAtLeast.increase(gc);
                // We might have already consumed all the letters in a wrong place match
                if (commonLettersRemaining.occurrences(gc) > 0) {
                    commonLettersRemaining.decrease(gc);
                }
            } else if (comparison.getIncorrectPositions()[i]) {
                int remaining = commonLettersRemaining.occurrences(gc);
                if (remaining > 0) {
                    addRule(Rule.createWrongPlace(gc, i));
                    mustHaveAtLeast.increase(gc);
                    commonLettersRemaining.decrease(gc);
                } else {
                    addRule(Rule.createNoMatch(gc, i));
                }
            } else if (comparison.getMismatches()[i]) {
                addRule(Rule.createMustNotHave(gc));
                addRule(Rule.createNoMatch(gc, i));
            }
        }

        // Derive character occurrence minimums from rules we've already created
        mustHaveAtLeast.getFrequencies().forEach((ch, min) -> {
            addRule(Rule.createMustHaveAtLeast(ch, min));
        });
    }

    private Rule combine (Rule r1, Rule r2) {
        if (r1 == null) {
            return r2;
        }
        if (r2 == null) {
            return r1;
        }
        return r1.combine(r2);
    }

    private void addRule(Rule r) {
        switch(r.getType()) {
            case MATCH -> matches[r.getPosition()] =  combine(r, matches[r.getPosition()]);
            case WRONG_PLACE ->  wrongPlaces[r.getPosition()] = combine(r, wrongPlaces[r.getPosition()]);
            case NO_MATCH -> noMatches[r.getPosition()] = combine(r, noMatches[r.getPosition()]);
            case MUST_NOT_HAVE -> mustNotHave = combine(r, mustNotHave);
            case MUST_HAVE_AT_LEAST -> {
                char firstAndOnlyChar = r.getCharactersAsString().charAt(0);
                mustHaveAtLeast.put(firstAndOnlyChar, combine(r, mustHaveAtLeast.get(firstAndOnlyChar)));
            }
        }
        allRules.add(r);
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        final String[] matchedChars = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = matches[i];
            if (r != null) {
                matchedChars[i] = r.getCharactersAsString().toUpperCase();
            }
        }
        output.append ("Matched{");
        Arrays.stream(matchedChars).forEach(output::append);
        output.append("} WrongPlace{");

        final String[] wrongPlacesArr = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = wrongPlaces[i];
            if (r != null) {
                wrongPlacesArr[i] = r.getCharactersAsString().toUpperCase();
            }
        }
        output.append(String.join(",", wrongPlacesArr));

        output.append("} NoMatch{");

        final String[] noMatchesArr = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = noMatches[i];
            if (r != null) {
                noMatchesArr[i] = r.getCharactersAsString().toUpperCase();
            }
        }
        output.append(String.join(",", noMatchesArr));

        output.append("} MustNotHave{");

        if (mustNotHave != null) {
            mustNotHave.getCharacters().forEach(ch -> {
                output.append(Character.toUpperCase(ch));
            });
        }
        output.append("} MustHaveAtLeast{");

        CharacterFrequency mustHaveAtLeastFreq = new CharacterFrequency();
        mustHaveAtLeast.forEach((ch, r) -> {
            mustHaveAtLeastFreq.set(ch, Math.max(mustHaveAtLeastFreq.occurrences(ch), r.getAmount()));
        });

        mustHaveAtLeast.forEach((ch, r) -> {
            mustHaveAtLeastFreq.set(ch, Math.max(mustHaveAtLeastFreq.occurrences(ch), r.getAmount()));
        });

        AtomicBoolean first = new AtomicBoolean(true);
        mustHaveAtLeastFreq.getFrequencies().forEach((ch, atLeast) -> {
            if (!first.get()) {
                output.append(",");
            }
            output.append(Character.toUpperCase(ch)).append(':').append(atLeast);
            first.set(false);
        });

        output.append("}");
        return output.toString();
    }

    public boolean match(Word word) {
        for (Rule rule : allRules) {
            if (!rule.match(word)) {
                return false;
            }
        }
        return true;
    }

    public RuleSet combineRuleSet(RuleSet other) {
        RuleSet result = new RuleSet(this);
        other.allRules.forEach(result::addRule);
        return result;
    }

    //todo better name?
    public float getCertainty() {
        // Lower uncertainty means more likely we are correct
        float certainty = 0;
        for (Rule rule : allRules) {
            switch (rule.getType()) {
                case MATCH -> certainty++;
            }
        }
        return certainty;
    }
}
