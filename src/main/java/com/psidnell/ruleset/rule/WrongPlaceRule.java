package com.psidnell.ruleset.rule;

import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.util.CharacterSet;
import com.psidnell.words.Word;

public class WrongPlaceRule extends AbstractRule {
    private final CharacterSet chars;
    private final int position;

    public WrongPlaceRule(Character ch, int position) {
        CharacterSet chars = new CharacterSet();
        chars.add(ch);
        this(chars, position);
    }

    public WrongPlaceRule(CharacterSet chars, int position) {
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

        CharacterSet combinedChars = new CharacterSet(getCharacters());
        combinedChars.addAll(other.getCharacters());
        return new WrongPlaceRule(combinedChars, position);
    }

    private static final Probe PROBE = Profiler.getProbe(WrongPlaceRule.class, "match");

    @Override
    public boolean match(Word word) {
        try (Probe p = PROBE.open()) {
            return !chars.contains(word.charAt(getPosition()));
        }
    }

    public int getPosition() {
        return position;
    }

    public CharacterSet getCharacters() {
        return chars;
    }


    public String getCharactersAsString() {
        return chars.toString();
    }
    @Override
    public String toString() {
        return chars + "![" + position + "]";
    }
}
