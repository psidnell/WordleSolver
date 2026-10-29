package com.psidnell.rules;

import com.psidnell.Word;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

//todo differing subclasses for types?
public class Rule implements Comparable<Rule> {
    private final RuleType type;
    private final Set<Character> chars = new TreeSet<>();
    private final String charsAsString;
    private final int position;
    private final int amount;

    public static Rule createMatch(Character ch, int position) {
        return new Rule(RuleType.MATCH, ch, position, -1);
    }

    public static Rule createNoMatch(Character ch, int position) {
        return new Rule(RuleType.NO_MATCH, ch, position, -1);
    }

    public static Rule createWrongPlace(Character ch, int position) {
        return new Rule(RuleType.WRONG_PLACE, ch, position, -1);
    }

    public static Rule createMustNotHave(Character ch) {
        return new Rule(RuleType.MUST_NOT_HAVE, ch, -1, -1);
    }

    public static Rule createMustHaveAtLeast(Character ch,  int amount) {
        return new Rule(RuleType.MUST_HAVE_AT_LEAST, ch, -1, amount);
    }

    private Rule(RuleType type, Character ch, int position, int amount) {
        this.type = type;
        this.chars.add(ch);
        this.position = position;
        this.amount = amount;
        this.charsAsString = ch.toString();
    }

    private Rule(RuleType type, Set<Character> chars, int position, int amount) {
        this.type = type;
        this.chars.addAll(chars);
        this.position = position;
        this.amount = amount;
        StringBuilder b = new StringBuilder();
        chars.forEach(b::append);
        this.charsAsString = b.toString();
    }

    public Rule combine(Rule other) {
        if (!this.type.equals(other.type)) {
            throw new IllegalArgumentException("Cannot combine with different types " + type + " and " + other.type);
        }
        if (this.position != other.position) {
            throw new IllegalArgumentException("Cannot combine with different positions " + position + " and " + other.position);
        }

        switch(type) {
            case MATCH:
                if (!charsAsString.equals(other.charsAsString)) {
                    throw new IllegalArgumentException("Cannot combine different matches " + charsAsString + " and " + other.charsAsString);
                }
                return createMatch(charsAsString.charAt(0), position);
            case NO_MATCH: {
                Set<Character> combinedChars = new HashSet<>(chars);
                combinedChars.addAll(other.chars);
                return new Rule(RuleType.NO_MATCH, combinedChars, position, amount);
            }
            case WRONG_PLACE: {
                Set<Character> combinedChars = new HashSet<>(chars);
                combinedChars.addAll(other.chars);
                return new Rule(RuleType.WRONG_PLACE, combinedChars, position, amount);
            }
            case MUST_NOT_HAVE: {
                Set<Character> combinedChars = new HashSet<>(chars);
                combinedChars.addAll(other.chars);
                return new Rule(RuleType.MUST_NOT_HAVE, combinedChars, position, amount);
            }
            case MUST_HAVE_AT_LEAST: {
                if (!charsAsString.equals(other.charsAsString)) {
                    throw new IllegalArgumentException("Cannot combine two " + type + " with differing chars " + charsAsString + ", " + other.charsAsString);
                }
                return new Rule(RuleType.MUST_HAVE_AT_LEAST, chars, position, Math.max(amount, other.amount));
            }
            default:
                throw new IllegalArgumentException("Unkown rule type: " + type);
        }
    }

    public boolean match(Word word) {
        return switch (type) {
            case MATCH -> chars.contains(word.charAt(position));
            case NO_MATCH -> !chars.contains(word.charAt(position));
            case WRONG_PLACE -> wrongPlace(word);
            case MUST_NOT_HAVE -> mustNotHave(word);
            case MUST_HAVE_AT_LEAST ->mustHaveAtLeast(word);
        };
    }

    private boolean mustHaveAtLeast(Word word) {
        for (Character ch : chars) {
            boolean mustHaveAtLeastMatch = word.occurencesOf(ch) >= amount;
            if (!mustHaveAtLeastMatch) {
                return false;
            }
        }
        return true;
    }

    private boolean mustNotHave(Word word) {
        for (Character ch : chars) {
            boolean mustNotHaveMatch = word.occurencesOf(ch) == 0;
            if (!mustNotHaveMatch) {
                return false;
            }
        }
        return true;
    }

    private boolean wrongPlace(Word word) {
        for (Character ch : chars) {
            //todo 2nd term necessary? we added a rule for that
            boolean wrongPlaceMatch = !word.charAt(position).equals(ch) && word.occurencesOf(ch) > 0;
            if (!wrongPlaceMatch) {
                return false;
            }
        }
        return true;
    }

    public RuleType getType () {
        return type;
    }

    public String getCharactersAsString() {
        return charsAsString;
    }

    public Set<Character> getCharacters() {
        return chars;
    }

    public int getPosition () {
        return position;
    }

    public int getAmount () {
        return amount;
    }

    @Override
    public String toString() {
        String chrsUpper = charsAsString.toUpperCase();
        return switch (type) {
            case MATCH -> chrsUpper + "*[" + position + "]";
            case NO_MATCH -> chrsUpper + "![" + position + "]";
            case WRONG_PLACE -> chrsUpper + "?[" + position + "]";
            case MUST_NOT_HAVE -> chrsUpper + "!";
            case MUST_HAVE_AT_LEAST -> chrsUpper + ">=" + amount;
        };
    }

    @Override
    public int compareTo(Rule o) {
        int result;

        if (position == -1 && o.position != -1) {
            return 1;
        }

        if (position != -1 && o.position == -1) {
            return -1;
        }

        result = Integer.compare(position, o.position);
        if (result != 0) {
            return result;
        }

        result = type.compareTo(o.type);
        if (result != 0) {
            return result;
        }

        result = charsAsString.compareTo(o.charsAsString);
        if (result != 0) {
            return result;
        }

        return Integer.compare(amount, o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return position == rule.position && amount == rule.amount && type == rule.type && Objects.equals(charsAsString, rule.charsAsString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, charsAsString, position, amount);
    }
}
