package com.psidnell.ruleset.rule;

import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.words.Word;

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

        return new MatchRule(getCharacter(), getPosition());
    }

    private static final Probe PROBE = Profiler.getProbe(MatchRule.class, "match");
    @Override
    public boolean match(Word word) {
        try (Probe p = PROBE.open()) {
            return ch.equals(word.charAt(position));
        }
    }

    public int getPosition() {
        return position;
    }

    public Character getCharacter() {
        return ch;
    }

    @Override
    public String toString() {
        return getCharacter() + "*[" + position + "]";
    }
}
