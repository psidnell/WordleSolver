package com.psidnell.ruleset.rule;

import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.words.Word;

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

        if (!ch.equals(other.ch)) {
            throw new IllegalArgumentException("Cannot combine different matches " + ch + " and " + other.ch);
        }

        return new MustHaveAtMostRule(ch, Math.min(amount, other.amount));
    }

    private static final Probe PROBE = Profiler.getProbe(MustHaveAtMostRule.class, "match");

    @Override
    public boolean match(Word word) {
        try (Probe p = PROBE.open()) {
            boolean mustHaveAtLeastMatch = word.occurrencesOf(ch) <= amount;
            if (!mustHaveAtLeastMatch) {
                return false;
            }

            return true;
        }
    }

    public int getAmount() {
        return amount;
    }

    public Character getCharacter() {
        return ch;
    }

    @Override
    public String toString() {
        return ch + "<=" + amount;
    }
}
