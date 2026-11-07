package com.psidnell.ruleset.rule;

import java.util.TreeSet;

public abstract class AbstractRule implements Rule {
    protected final RuleType type;

    protected AbstractRule(RuleType type) {
        this.type = type;
    }

    public RuleType getType() {
        return type;
    }

    protected static String charactersToString(TreeSet<Character> chars) {
        StringBuilder b = new StringBuilder(chars.size());
        chars.forEach(b::append);
        return b.toString();
    }
}
