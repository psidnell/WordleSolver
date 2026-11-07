package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.Set;
import java.util.TreeSet;

public class WrongPlaceRule extends AbstractRule {
    private final TreeSet<Character> chars;
    private final int position;

    public WrongPlaceRule(Character ch, int position) {
        TreeSet<Character> chars = new TreeSet<>();
        chars.add(ch);
        this(chars, position);
    }

    public WrongPlaceRule(TreeSet<Character> chars, int position) {
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

        TreeSet<Character> combinedChars = new TreeSet<>(getCharacters());
        combinedChars.addAll(other.getCharacters());
        return new WrongPlaceRule(combinedChars, position);
    }

    @Override
    public boolean match(Word word) {
        return !chars.contains(word.charAt(getPosition()));
    }

    public int getPosition() {
        return position;
    }

    public Set<Character> getCharacters() {
        return chars;
    }


    public String getCharactersAsString() {
        return charactersToString(chars);
    }
    @Override
    public String toString() {
        return charactersToString(chars) + "![" + position + "]";
    }


}
