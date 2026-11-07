package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.Set;

public class MustHaveAtMostRule extends AbstractRule {
    private final Character ch;
    private final int amount;

    public MustHaveAtMostRule(Character ch, int amount) {
        super(RuleType.MUST_HAVE_AT_MOST);
        this.ch = ch;
        this.amount = amount;
    }

    @Override
    public Rule combine(Rule o) {
        MustHaveAtMostRule other = (MustHaveAtMostRule) o;

        if (!getCharactersAsString().equals(other.getCharactersAsString())) {
            throw new IllegalArgumentException("Cannot combine different matches " + ch + " and " + other.ch);
        }

        return new MustHaveAtMostRule(ch, Math.min(amount, other.amount));
    }

    @Override
    public boolean match(Word word) {
        boolean mustHaveAtLeastMatch = word.occurrencesOf(ch) <= amount;
        if (!mustHaveAtLeastMatch) {
            return false;
        }

        return true;
    }

    @Override
    public int getPosition() {
        return -1;
    }

    public String getCharactersAsString() {
        // TODO cache in constructor?
        return ch.toString();
    }

    @Override
    public int getAmount() {
        return amount;
    }

    public Character getCharacter() {
        return ch;
    }

    @Override
    public Set<Character> getCharacters() {
        return Set.of(ch);
    }

    @Override
    public String toString() {
        return ch + "<=" + amount;
    }
}
