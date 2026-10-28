package com.psidnell.rules;

import com.psidnell.Word;

import java.util.Objects;

//todo wrap constructor with static method for rule

public class Rule implements Comparable<Rule> {
    private final RuleType type;
    private final Character ch;
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
        this.ch = ch;
        this.position = position;
        this.amount = amount;
    }

    public boolean match(Word word) {
        return switch (type) {
            case MATCH -> word.charAt(position).equals(ch);
            case NO_MATCH -> !word.charAt(position).equals(ch);
            case WRONG_PLACE -> !word.charAt(position).equals(ch) && word.occurencesOf(ch) > 0; //todo 2nd term necessary? we added a rule for that
            case MUST_NOT_HAVE -> word.occurencesOf(ch) == 0;
            case MUST_HAVE_AT_LEAST -> word.occurencesOf(ch) >= amount;
        };
    }

    public RuleType getType () {
        return type;
    }

    public Character getCharacter () {
        return ch;
    }

    public int getPosition () {
        return position;
    }

    public int getAmount () {
        return amount;
    }

    @Override
    public String toString() {
        Character chUpper = Character.toUpperCase(ch);
        return switch (type) {
            case MATCH -> chUpper + "M[" + position + "]";
            case NO_MATCH -> chUpper +"X[" + position + "]";
            case WRONG_PLACE -> chUpper + "?[" + position + "]";
            case MUST_NOT_HAVE -> chUpper + "!";
            case MUST_HAVE_AT_LEAST -> chUpper + ">=" + amount;
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

        result = ch.compareTo(o.ch);
        if (result != 0) {
            return result;
        }

        return Integer.compare(amount, o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return position == rule.position && amount == rule.amount && type == rule.type && Objects.equals(ch, rule.ch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, ch, position, amount);
    }
}
