package com.psidnell.ruleset;

import com.psidnell.PlayerException;
import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.ruleset.rule.MatchRule;
import com.psidnell.ruleset.rule.MustHaveAtLeastRule;
import com.psidnell.ruleset.rule.MustHaveAtMostRule;
import com.psidnell.ruleset.rule.MustNotHaveRule;
import com.psidnell.ruleset.rule.NotRule;
import com.psidnell.ruleset.rule.Rule;
import com.psidnell.ruleset.rule.WrongPlaceRule;
import com.psidnell.words.Word;
import com.psidnell.words.WordComparison;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RuleSet {

    private final MatchRule[] matches = new MatchRule[5];
    private final WrongPlaceRule[] wrongPlaces = new WrongPlaceRule[5];
    private final NotRule[] not = new NotRule[5];
    private final Map<Character, MustHaveAtLeastRule> mustHaveAtLeast = new TreeMap<>();
    private final Map<Character, MustHaveAtMostRule> mustHaveAtMost = new TreeMap<>();
    private MustNotHaveRule mustNotHave = null;

    public RuleSet() {
    }

    public RuleSet(RuleSet other) {
        other.allRules().forEach(this::addRule);
    }


    private static final Probe PROBE = Profiler.getProbe(RuleSet.class, "Constructor(Word,Word)");

    /**
     * When we provide a guess against a know solution, create a rule set
     * that encapsulates what we learned from the guess.
     *
     * @param guess
     * @param solution
     */
    public RuleSet(Word guess, Word solution) {
        try (Probe p = PROBE.open()) {
            WordComparison comparison = guess.compareWords(solution);
            CharacterFrequency commonLettersRemaining = new CharacterFrequency(comparison.getCommonLetterFrequency());
            CharacterFrequency mustHaveAtLeast = new CharacterFrequency();
            CharacterFrequency seenSoFar = new CharacterFrequency();

            for (int i = 0; i < 5; i++) {
                Character gc = guess.charAt(i);
                seenSoFar.increase(gc);
                if (comparison.getCorrectPositions()[i]) {
                    addRule(new MatchRule(gc, i));
                    // We might have already consumed all the letters in a wrong place match
                    int remaining = commonLettersRemaining.occurrences(gc);
                    if (remaining > 0) {
                        mustHaveAtLeast.increase(gc);
                        commonLettersRemaining.decrease(gc);
                    } else {
                        addRule(new MustHaveAtMostRule(gc, seenSoFar.occurrences(gc) - 1));
                    }
                } else if (comparison.getIncorrectPositions()[i]) {
                    int remaining = commonLettersRemaining.occurrences(gc);
                    if (remaining > 0) {
                        addRule(new WrongPlaceRule(gc, i));
                        mustHaveAtLeast.increase(gc);
                        commonLettersRemaining.decrease(gc);
                    } else {
                        addRule(new NotRule(gc, i));
                        addRule(new MustHaveAtMostRule(gc, seenSoFar.occurrences(gc) - 1));
                    }
                } else if (comparison.getMismatches()[i]) {
                    addRule(new MustNotHaveRule(gc));
                    addRule(new NotRule(gc, i));
                }
            }

            // Can derive this
            mustHaveAtLeast.frequencies().forEach((ch, min) -> {
                addRule(new MustHaveAtLeastRule(ch, min));
            });
        }
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
                Character ch1 = guess.charAt(i);
                addRule(new NotRule(ch1, i));
                Character ch2 = guess.charAt(i);
                addRule(new WrongPlaceRule(ch2, i));
            } else if (Character.isUpperCase(ch)) {
                addRule(new MatchRule(chUc, i));
            } else {
                addRule(new WrongPlaceRule(chUc, i));
            }
        }

        answerFrequencies.frequencies().forEach((ch, answerOccurrences) -> {
            addRule(new MustHaveAtLeastRule(ch, answerOccurrences));
            if (answerOccurrences < guessFrequencies.occurrences((ch))) {
                addRule(new MustHaveAtMostRule(ch, answerOccurrences));
            }
        });

        for (int i = 0; i < 5; i++) {
            Character ch = guess.charAt(i);
            if (answerFrequencies.occurrences(ch) == 0) {
                addRule(new MustNotHaveRule(guess.charAt(i)));
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
    // TODO This looks horrible
    private void addRule(Rule r) {
        switch (r.getType()) {
            case MATCH -> matches[((MatchRule)r).getPosition()] = (MatchRule) combine(r, matches[((MatchRule)r).getPosition()]);
            case WRONG_PLACE -> wrongPlaces[((WrongPlaceRule)r).getPosition()] = (WrongPlaceRule) combine(r, wrongPlaces[((WrongPlaceRule)r).getPosition()]);
            case NOT -> not[((NotRule)r).getPosition()] = (NotRule) combine(r, not[((NotRule)r).getPosition()]);
            case MUST_NOT_HAVE -> mustNotHave = (MustNotHaveRule) combine(r, mustNotHave);
            case MUST_HAVE_AT_LEAST -> {
                char firstAndOnlyChar = ((MustHaveAtLeastRule)r).getCharacter();
                mustHaveAtLeast.put(firstAndOnlyChar, (MustHaveAtLeastRule) combine(r, mustHaveAtLeast.get(firstAndOnlyChar)));
            }
            case MUST_HAVE_AT_MOST -> {
                char firstAndOnlyChar = ((MustHaveAtMostRule)r).getCharacter();
                mustHaveAtMost.put(firstAndOnlyChar, (MustHaveAtMostRule) combine(r, mustHaveAtMost.get(firstAndOnlyChar)));
            }
        }
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        final Character[] matchedChars = new Character[]{'_', '_', '_', '_', '_'};
        for (int i = 0; i < 5; i++) {
            Rule r = matches[i];
            if (r != null) {
                matchedChars[i] = ((MatchRule)r).getCharacter();
            }
        }
        output.append("Matched{");
        Arrays.stream(matchedChars).forEach(output::append);
        output.append("} WrongPlace{");

        final String[] wrongPlacesArr = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = wrongPlaces[i];
            if (r != null) {
                wrongPlacesArr[i] = ((WrongPlaceRule)r).getCharactersAsString();
            }
        }
        output.append(String.join(",", wrongPlacesArr));

        output.append("} Not{");

        final String[] noMatchesArr = new String[]{"_", "_", "_", "_", "_"};
        for (int i = 0; i < 5; i++) {
            Rule r = not[i];
            if (r != null) {
                noMatchesArr[i] = ((NotRule)r).getCharactersAsString();
            }
        }
        output.append(String.join(",", noMatchesArr));

        output.append("} None{");

        if (mustNotHave != null) {
            mustNotHave.getCharacters().forEach(output::append);
        }
        output.append("} AtLeast{");

        boolean first = true;
        for (MustHaveAtLeastRule r : mustHaveAtLeast.values()) {
            if (!first) {
                output.append(',');
            }
            Character firstChar = r.getCharacter();
            output.append(firstChar).append(':').append(r.getAmount());
            first = false;
        }

        output.append("} AtMost{");

        first = true;
        for (MustHaveAtMostRule r : mustHaveAtMost.values()) {
            if (!first) {
                output.append(',');
            }
            Character firstChar = ((MustHaveAtMostRule)r).getCharacter();
            output.append(firstChar).append(':').append(r.getAmount());
            first = false;
        }

        return output.append('}').toString();
    }

    private static final Probe PROBE2 = Profiler.getProbe(RuleSet.class, "match");

    public boolean match(Word word) {
        try (Probe p = PROBE2.open()) {
            for (Rule rule : allRules()) {
                if (!rule.match(word)) {
                    return false;
                }
            }
            return true;
        }
    }

    public RuleSet combineRuleSet(RuleSet other) {
        RuleSet result = new RuleSet(this);
        other.allRules().forEach(result::addRule);
        return result;
    }

    private void addNotNull(Rule r, List<Rule> rules) {
        if (r != null) {
            rules.add(r);
        }
    }

    private List<Rule> allRules() {
        List<Rule> result = new LinkedList<>();
        Arrays.asList(matches).forEach(r -> addNotNull(r, result));
        Arrays.asList(wrongPlaces).forEach(r -> addNotNull(r, result));
        Arrays.asList(not).forEach(r -> addNotNull(r, result));
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
