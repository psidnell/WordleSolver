package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.Set;

public class MustHaveAtLeastRule extends AbstractRule {
    private final Character ch;
    private final int amount;

    public MustHaveAtLeastRule(Character ch, int amount) {
        super(RuleType.MUST_HAVE_AT_LEAST);
        this.ch = ch;
        this.amount = amount;
    }

    @Override
    public Rule combine(Rule o) {
        MustHaveAtLeastRule other = (MustHaveAtLeastRule) o;

        if (!getCharactersAsString().equals(other.getCharactersAsString())) {
            throw new IllegalArgumentException("Cannot combine different matches " + ch + " and " + other.ch);
        }

        return new MustHaveAtLeastRule(ch, Math.max(amount, other.amount));
    }

    @Override
    public int getPosition() {
        return -1;
    }

    @Override
    public String getCharactersAsString() {
        // TODO cache in constructor?
        return ch.toString();
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Set<Character> getCharacters() {
        return Set.of(ch);
    }

    @Override
    public boolean match(Word word) {
        boolean mustHaveAtLeastMatch = word.occurrencesOf(ch) >= amount;
        if (!mustHaveAtLeastMatch) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return ch + ">=" + amount;
    }
}
