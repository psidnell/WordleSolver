package com.psidnell.rules;

import com.psidnell.PlayerException;
import com.psidnell.words.Word;
import com.psidnell.words.WordComparison;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class RuleSet {

    private final Rule[] matches = new Rule[5];
    private final Rule[] wrongPlaces = new Rule[5];
    private final Rule[] noMatches = new Rule[5];
    private final Map<Character, Rule> mustHaveAtLeast = new TreeMap<>();
    private final Map<Character, Rule> mustHaveAtMost = new TreeMap<>();
    private Rule mustNotHave = null;

    public RuleSet() {
    }

    public RuleSet(RuleSet other) {
        other.allRules().forEach(this::addRule);
    }

    /**
     * When we provide a guess against a know solution, create a rule set
     * that encapsulates what we learned from the guess.
     *
     * @param guess
     * @param solution
     */
    public RuleSet(Word guess, Word solution) {
        WordComparison comparison = guess.compareWords(solution);
        CharacterFrequency commonLettersRemaining = new CharacterFrequency(comparison.getCommonLetterFrequency());
        CharacterFrequency mustHaveAtLeast = new CharacterFrequency();
        CharacterFrequency seenSoFar = new CharacterFrequency();

        for (int i = 0; i < 5; i++) {
            Character gc = guess.charAt(i);
            seenSoFar.increase(gc);
            if (comparison.getCorrectPositions()[i]) {
                addRule(Rule.createMatch(gc, i));
                // We might have already consumed all the letters in a wrong place match
                int remaining = commonLettersRemaining.occurrences(gc);
                if (remaining > 0) {
                    mustHaveAtLeast.increase(gc);
                    commonLettersRemaining.decrease(gc);
                } else {
                    addRule(Rule.createMustHaveAtMost(gc, seenSoFar.occurrences(gc) - 1));
                }
            } else if (comparison.getIncorrectPositions()[i]) {
                int remaining = commonLettersRemaining.occurrences(gc);
                if (remaining > 0) {
                    addRule(Rule.createWrongPlace(gc, i));
                    mustHaveAtLeast.increase(gc);
                    commonLettersRemaining.decrease(gc);
                } else {
                    addRule(Rule.createNoMatch(gc, i));
                    addRule(Rule.createMustHaveAtMost(gc, seenSoFar.occurrences(gc) - 1));
                }
            } else if (comparison.getMismatches()[i]) {
                addRule(Rule.createMustNotHave(gc));
                addRule(Rule.createNoMatch(gc, i));
            }
        }

        // Can derive this
        mustHaveAtLeast.frequencies().forEach((ch, min) -> {
            addRule(Rule.createMustHaveAtLeast(ch, min));
        });
    }

    /**
     * Can provide the answer to a guess.
     * Upper case means we got the character in the right position.
     * Lower case means we got it in the wrong position.
     * '_' means that there was no match
     *
     * @param answer
     */
    public RuleSet(Word guess, String answer) {
        if (answer.length() != 5) {
            throw new PlayerException("Must be 5 characters long");
        }

        CharacterFrequency guessFrequencies = guess.geFrequencies();
        CharacterFrequency answerFrequencies = new CharacterFrequency();

        for (int i = 0; i < 5; i++) {
            Character ch = answer.charAt(i);
            if (!ch.equals('_')) {
                Character chUc = Character.toUpperCase(answer.charAt(i));
                answerFrequencies.increase(chUc);
            }
        }

        for (int i = 0; i < 5; i++) {
            Character ch = answer.charAt(i);
            Character chUc = Character.toUpperCase(ch);
            if (ch.equals('_')) {
                addRule(Rule.createNoMatch(guess.charAt(i), i));
                addRule(Rule.createWrongPlace(guess.charAt(i), i));
            } else if (Character.isUpperCase(ch)) {
                addRule(Rule.createMatch(chUc, i));
            } else {
                addRule(Rule.createWrongPlace(chUc, i));
            }
        }

        answerFrequencies.frequencies().forEach((ch, answerOccurrences) -> {
            addRule(Rule.createMustHaveAtLeast(ch, answerOccurrences));
            if (answerOccurrences < guessFrequencies.occurrences((ch))) {
                addRule(Rule.createMustHaveAtMost(ch, answerOccurrences));
            }
        });

        for (int i = 0; i < 5; i++) {
            Character ch = guess.charAt(i);
            if (answerFrequencies.occurrences(ch) == 0) {
                addRule(Rule.createMustNotHave(guess.charAt(i)));
            }
        }
    }

    private Rule combine(Rule r1, Rule r2) {
        if (r1 == null) {
            return r2;
        }
        if (r2 == null) {
            return r1;
        }
        return r1.combine(r2);
    }

    /**
     * Add the rule by optimally combining it with existing rules.
     *
     * @param r
     */
    private void addRule(Rule r) {
        switch (r.getType()) {
            case MATCH -> matches[r.getPosition()] = combine(r, matches[r.getPosition()]);
            case WRONG_PLACE -> wrongPlaces[r.getPosition()] = combine(r, wrongPlaces[r.getPosition()]);
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
                matchedChars[i] = r.getCharactersAsString();
            }
        }
        output.append("Matched{");
        Arrays.stream(matchedChars).forEach(output::append);
        output.append("} WrongPlace{");

        final String[] wrongPlacesArr = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = wrongPlaces[i];
            if (r != null) {
                wrongPlacesArr[i] = r.getCharactersAsString();
            }
        }
        output.append(String.join(",", wrongPlacesArr));

        output.append("} NoMatch{");

        final String[] noMatchesArr = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = noMatches[i];
            if (r != null) {
                noMatchesArr[i] = r.getCharactersAsString();
            }
        }
        output.append(String.join(",", noMatchesArr));

        output.append("} None{");

        if (mustNotHave != null) {
            mustNotHave.getCharacters().forEach(output::append);
        }
        output.append("} AtLeast{");

        boolean first = true;
        for (Rule r : mustHaveAtLeast.values()) {
            if (!first) {
                output.append(',');
            }
            Character firstChar = r.getCharactersAsString().charAt(0);
            output.append(firstChar).append(':').append(r.getAmount());
            first = false;
        }

        output.append("} AtMost{");

        first = true;
        for (Rule r : mustHaveAtMost.values()) {
            if (!first) {
                output.append(',');
            }
            Character firstChar = r.getCharactersAsString().charAt(0);
            output.append(firstChar).append(':').append(r.getAmount());
            first = false;
        }

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
        result.addAll(mustHaveAtMost.values());
        return result;
    }

    public boolean isSolved() {
        int count = 0;
        for (Rule r : matches) {
            if (r != null) {
                count++;
            }
        }
        return count == 5;
    }
}
