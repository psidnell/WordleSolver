package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.Set;
import java.util.TreeSet;

public class NotRule extends AbstractRule {
    //todo Make it an array
    private final TreeSet<Character> chars;
    private final int position;

    public NotRule(Character ch, int position) {
        TreeSet<Character> chars = new TreeSet<>();
        chars.add(ch);
        this(chars, position);
    }

    public NotRule(TreeSet<Character> chars, int position) {
        super(RuleType.NOT);
        this.chars = chars;
        this.position = position;
    }

    @Override
    public Rule combine(Rule o) {
        NotRule other = (NotRule) o;

        if (this.getPosition() != other.getPosition()) {
            throw new IllegalArgumentException("Cannot combine with different positions " + position + " and " + position);
        }

        TreeSet<Character> combinedChars = new TreeSet<>(getCharacters());
        combinedChars.addAll(other.getCharacters());
        return new NotRule(combinedChars, position);
    }

    @Override
    public boolean match(Word word) {
        return !chars.contains(word.charAt(getPosition()));
    }

    public int getPosition() {
        return position;
    }

    public String getCharactersAsString() {
        // TODO cache in constructor?
        StringBuilder b = new StringBuilder(chars.size());
        chars.forEach(b::append);
        return b.toString();
    }

    public Set<Character> getCharacters() {
        return chars;
    }

    @Override
    public String toString() {
        return getCharactersAsString() + "![" + position + "]";
    }
}
