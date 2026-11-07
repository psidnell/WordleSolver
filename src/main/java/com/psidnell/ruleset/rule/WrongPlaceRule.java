package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class WrongPlaceRule extends AbstractRule {
    private final Set<Character> chars;
    private final int position;

    public WrongPlaceRule(Character ch, int position) {
        TreeSet<Character> chars = new TreeSet<>();
        chars.add(ch);
        this(chars, position);
    }

    public WrongPlaceRule(Set<Character> chars, int position) {
        super(RuleType.WRONG_PLACE);
        this.chars = chars;
        this.position = position;
    }

    @Override
    public Rule combine(Rule o) {
        WrongPlaceRule other = (WrongPlaceRule) o;

        if (this.getPosition() != other.getPosition()) {
            throw new IllegalArgumentException("Cannot combine with different positions " + position + " and " + position);
        }

        Set<Character> combinedChars = new HashSet<>(getCharacters());
        combinedChars.addAll(other.getCharacters());
        return new WrongPlaceRule(combinedChars, position);
    }

    @Override
    public boolean match(Word word) {
        return !chars.contains(word.charAt(getPosition()));
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getCharactersAsString() {
        // TODO cache in constructor?
        StringBuilder b = new StringBuilder(chars.size());
        chars.forEach(b::append);
        return b.toString();
    }

    @Override
    public int getAmount() {
        return -1;
    }

    @Override
    public Set<Character> getCharacters() {
        return chars;
    }

    @Override
    public String toString() {
        return getCharactersAsString() + "![" + getPosition() + "]";
    }
}
