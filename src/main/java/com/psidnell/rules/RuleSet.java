package com.psidnell.rules;

import com.psidnell.Word;
import com.psidnell.WordComparison;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RuleSet {

    private final Rule[] matches = new Rule[5];
    private final Rule[] wrongPlaces = new Rule[5];
    private final Rule[] noMatches = new Rule[5];;
    private Rule mustNotHave = null;
    private final Map<Character, Rule> mustHaveAtLeast = new TreeMap<>();
    private final Map<Character, Rule> mustHaveAtMost = new TreeMap<>();

    public RuleSet() {
    }

    public RuleSet(RuleSet other) {
        other.allRules().forEach(this::addRule);
    }

    /**
     * When we provide a guess against a know solution, create a rule set
     * that encapsulates what we learned from the guess.
     * @param guess
     * @param solution
     */
    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        CharacterFrequency commonLettersRemaining = new CharacterFrequency(comparison.getCommonLetterFrequency());
        CharacterFrequency mustHaveAtLeast = new CharacterFrequency();
        CharacterFrequency mustHaveAtMost = new CharacterFrequency();
        // Handle basic character matches and misses
        for(int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            if (comparison.getCorrectPositions()[i]) {
                addRule(Rule.createMatch(gc, i));
                // We might have already consumed all the letters in a wrong place match
                if (commonLettersRemaining.occurrences(gc) > 0) {
                    mustHaveAtLeast.increase(gc);
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

        // Derive character occurrence max/min

        mustHaveAtLeast.getFrequencies().forEach((ch, min) -> {
            addRule(Rule.createMustHaveAtLeast(ch, min));
        });

//        commonLettersRemaining.getFrequencies().forEach((ch, max) -> {
//            addRule(Rule.createMustHaveAtMost(ch, max));
//        });
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
            case MUST_HAVE_AT_MOST -> {
                char firstAndOnlyChar = r.getCharactersAsString().charAt(0);
                mustHaveAtMost.put(firstAndOnlyChar, combine(r, mustHaveAtMost.get(firstAndOnlyChar)));
            }
        }
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

        output.append("} None{");

        if (mustNotHave != null) {
            mustNotHave.getCharacters().forEach(ch -> {
                output.append(Character.toUpperCase(ch));
            });
        }
        output.append("} AtLeast{");

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

        output.append("} AtMost{");

        CharacterFrequency mustHaveAtMostFreq = new CharacterFrequency();
        mustHaveAtMost.forEach((ch, r) -> {
            mustHaveAtMostFreq.set(ch, Math.max(mustHaveAtMostFreq.occurrences(ch), r.getAmount()));
        });

        mustHaveAtMost.forEach((ch, r) -> {
            mustHaveAtMostFreq.set(ch, Math.max(mustHaveAtMostFreq.occurrences(ch), r.getAmount()));
        });


        first.set(true);
        mustHaveAtMostFreq.getFrequencies().forEach((ch, atMost) -> {
            if (!first.get()) {
                output.append(",");
            }
            output.append(Character.toUpperCase(ch)).append(':').append(atMost);
            first.set(false);
        });


        return output.append('}').toString();
    }

    public boolean match(Word word) {
        for (Rule rule : allRules()) {
            if (!rule.match(word)) {
                return false;
            }
        }
        return true;
    }

    public RuleSet combineRuleSet(RuleSet other) {
        RuleSet result = new RuleSet(this);
        other.allRules().forEach(result::addRule);
        return result;
    }

    //todo better name?
    public float getCertainty() {
        // Lower uncertainty means more likely we are correct
        float certainty = 0;
        for (Rule rule : allRules()) {
            switch (rule.getType()) {
                case MATCH -> certainty++;
            }
        }
        return certainty;
    }

    private void addNotNull(Rule r, Set<Rule> rules) {
        if (r != null) {
            rules.add(r);
        }
    }

    private Set<Rule> allRules() {
        TreeSet<Rule> result = new TreeSet<>();
        Arrays.asList(matches).forEach(r -> addNotNull(r, result));
        Arrays.asList(wrongPlaces).forEach(r -> addNotNull(r, result));
        Arrays.asList(noMatches).forEach(r -> addNotNull(r, result));
        addNotNull(mustNotHave, result);
        result.addAll(mustHaveAtLeast.values());
        result.addAll( mustHaveAtMost.values());
        return result;
    }
}
