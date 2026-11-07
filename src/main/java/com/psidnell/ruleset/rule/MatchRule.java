package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.Set;

public class MatchRule extends AbstractRule {
    private final Character ch;
    private final int position;

    public MatchRule(Character ch, int position) {
        super(RuleType.MATCH);
        this.ch = ch;
        this.position = position;
    }

    @Override
    public Rule combine(Rule o) {
        MatchRule other = (MatchRule) o;

        if (this.getPosition() != other.getPosition()) {
            throw new IllegalArgumentException("Cannot combine with different positions " + position + " and " + position);
        }

        if (!ch.equals(other.ch)) {
            throw new IllegalArgumentException("Cannot combine different matches " + ch + " and " + other.ch);
        }

        return new MatchRule(getCharactersAsString().charAt(0), getPosition());
    }

    @Override
    public boolean match(Word word) {
        return ch.equals(word.charAt(position));
    }

    @Override
    public int getPosition() {
        return position;
    }

    public String getCharactersAsString() {
        // TODO cache in constructor?
        return ch.toString();
    }

    public Character getCharacter() {
        return ch;
    }

    @Override
    public int getAmount() {
        return -1;
    }

    @Override
    public Set<Character> getCharacters() {
        return Set.of(ch);
    }

    @Override
    public String toString() {
        return getCharactersAsString() + "*[" + position + "]";
    }
}
