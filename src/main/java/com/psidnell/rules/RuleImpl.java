package com.psidnell.rules;

import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.words.Word;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

// todo Could use subclasses
public class RuleImpl extends AbstractRule {
    private final Set<Character> chars = new TreeSet<>();
    private final String charsAsString;
    private final int position;
    private final int amount;

    RuleImpl(RuleType type, Character ch, int position, int amount) {
        super(type);
        this.chars.add(ch);
        this.position = position;
        this.amount = amount;
        this.charsAsString = ch.toString();
    }

    private RuleImpl(RuleType type, Set<Character> chars, int position, int amount) {
        super(type);
        this.chars.addAll(chars);
        this.position = position;
        this.amount = amount;
        StringBuilder b = new StringBuilder();
        chars.forEach(b::append);
        this.charsAsString = b.toString();
    }

    public Rule combine(Rule other) {
        if (!this.getType().equals(other.getType())) {
            throw new IllegalArgumentException("Cannot combine with different types " + getType() + " and " + other.getType());
        }
        if (this.getPosition() != other.getPosition()) {
            throw new IllegalArgumentException("Cannot combine with different positions " + getPosition() + " and " + other.getPosition());
        }

        switch (getType()) {
            case MATCH:
                if (!getCharactersAsString().equals(other.getCharactersAsString())) {
                    throw new IllegalArgumentException("Cannot combine different matches " + getCharactersAsString() + " and " + other.getCharactersAsString());
                }
                return Rule.createMatch(getCharactersAsString().charAt(0), getPosition());
            case NOT: {
                Set<Character> combinedChars = new HashSet<>(getCharacters());
                combinedChars.addAll(other.getCharacters());
                return new RuleImpl(RuleType.NOT, combinedChars, getPosition(), getAmount());
            }
            case WRONG_PLACE: {
                Set<Character> combinedChars = new HashSet<>(getCharacters());
                combinedChars.addAll(other.getCharacters());
                return new RuleImpl(RuleType.WRONG_PLACE, combinedChars, getPosition(), getAmount());
            }
            case MUST_NOT_HAVE: {
                Set<Character> combinedChars = new HashSet<>(getCharacters());
                combinedChars.addAll(other.getCharacters());
                return new RuleImpl(RuleType.MUST_NOT_HAVE, combinedChars, getPosition(), getAmount());
            }
            case MUST_HAVE_AT_LEAST: {
                if (!getCharactersAsString().equals(other.getCharactersAsString())) {
                    throw new IllegalArgumentException("Cannot combine two " + getType() + " with differing chars " + getCharactersAsString() + ", " + other.getCharactersAsString());
                }
                return new RuleImpl(RuleType.MUST_HAVE_AT_LEAST, getCharacters(), getPosition(), Math.max(getAmount(), other.getAmount()));
            }
            case MUST_HAVE_AT_MOST: {
                if (!getCharactersAsString().equals(other.getCharactersAsString())) {
                    throw new IllegalArgumentException("Cannot combine two " + getType() + " with differing chars " + getCharactersAsString() + ", " + other.getCharactersAsString());
                }
                return new RuleImpl(RuleType.MUST_HAVE_AT_MOST, getCharacters(), getPosition(), Math.min(getAmount(), other.getAmount()));
            }
            default:
                throw new IllegalArgumentException("Unkown rule type: " + getType());
        }
    }

    private static final Probe PROBE = Profiler.getProbe(Rule.class, "match");

    public boolean match(Word word) {
        try (Probe p = PROBE.open()) {
            return switch (getType()) {
                case MATCH -> getCharacters().contains(word.charAt(getPosition()));
                case NOT -> !getCharacters().contains(word.charAt(getPosition()));
                case WRONG_PLACE -> wrongPlace(word);
                case MUST_NOT_HAVE -> mustNotHave(word);
                case MUST_HAVE_AT_LEAST -> mustHaveAtLeast(word);
                case MUST_HAVE_AT_MOST -> mustHaveAtMost(word);
            };
        }
    }

    private boolean mustHaveAtLeast(Word word) {
        for (Character ch : getCharacters()) {
            boolean mustHaveAtLeastMatch = word.occurrencesOf(ch) >= getAmount();
            if (!mustHaveAtLeastMatch) {
                return false;
            }
        }
        return true;
    }

    private boolean mustHaveAtMost(Word word) {
        for (Character ch : getCharacters()) {
            boolean mustHaveAtLeastMatch = word.occurrencesOf(ch) <= getAmount();
            if (!mustHaveAtLeastMatch) {
                return false;
            }
        }
        return true;
    }

    private boolean mustNotHave(Word word) {
        for (Character ch : getCharacters()) {
            boolean mustNotHaveMatch = word.occurrencesOf(ch) == 0;
            if (!mustNotHaveMatch) {
                return false;
            }
        }
        return true;
    }

    private boolean wrongPlace(Word word) {
        for (Character ch : getCharacters()) {
            boolean matches = word.charAt(getPosition()).equals(ch);
            if (matches) {
                return false;
            }
        }
        return true;
    }

    public String getCharactersAsString() {
        return charsAsString;
    }

    public Set<Character> getCharacters() {
        return chars;
    }

    public int getPosition() {
        return position;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return switch (getType()) {
            case MATCH -> getCharactersAsString() + "*[" + getPosition() + "]";
            case NOT -> getCharactersAsString() + "![" + getPosition() + "]";
            case WRONG_PLACE -> getCharactersAsString() + "?[" + getPosition() + "]";
            case MUST_NOT_HAVE -> getCharactersAsString() + "!";
            case MUST_HAVE_AT_LEAST -> getCharactersAsString() + ">=" + getAmount();
            case MUST_HAVE_AT_MOST -> getCharactersAsString() + "<=" + getAmount();
        };
    }
}
