package com.psidnell.ruleset.rule;

import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.util.CharacterSet;
import com.psidnell.words.Word;

public class MustNotHaveRule extends AbstractRule {
    private final CharacterSet chars;

    public MustNotHaveRule(Character ch) {
        CharacterSet chars = new CharacterSet();
        chars.add(ch);
        this(chars);
    }

    public MustNotHaveRule(CharacterSet chars) {
        super(RuleType.MUST_NOT_HAVE);
        this.chars = chars;
    }

    private static final Probe PROBE = Profiler.getProbe(MustNotHaveRule.class, "match");

    @Override
    public Rule combine(Rule o) {
        try (Probe p = PROBE.open()) {
            MustNotHaveRule other = (MustNotHaveRule) o;

            CharacterSet combinedChars = new CharacterSet(chars);
            combinedChars.addAll(other.getCharacters());
            return new MustNotHaveRule(combinedChars);
        }
    }

    @Override
    public Rule copy() {
        return new MustNotHaveRule(new CharacterSet(chars));
    }

    @Override
    public boolean match(Word word) {
        for (Character ch : getCharacters().values()) {
            boolean mustNotHaveMatch = word.occurrencesOf(ch) == 0;
            if (!mustNotHaveMatch) {
                return false;
            }
        }
        return true;
    }

    public CharacterSet getCharacters() {
        return chars;
    }

    @Override
    public String toString() {
        return chars + "!";
    }
}
