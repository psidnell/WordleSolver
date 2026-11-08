package com.psidnell.ruleset.rule;

import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.util.CharacterSet;
import com.psidnell.words.Word;

public class NotRule extends AbstractRule {
    //todo Make it an array
    private final CharacterSet chars;
    private final int position;

    public NotRule(Character ch, int position) {
        CharacterSet chars = new CharacterSet();
        chars.add(ch);
        this(chars, position);
    }

    public NotRule(CharacterSet chars, int position) {
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

        CharacterSet combinedChars = new CharacterSet(getCharacters());
        combinedChars.addAll(other.getCharacters());
        return new NotRule(combinedChars, position);
    }

    private static final Probe PROBE = Profiler.getProbe(NotRule.class, "match");

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
