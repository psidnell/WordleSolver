package com.psidnell.ruleset.rule;

public abstract class AbstractRule implements Rule {
    protected final RuleType type;

    protected AbstractRule(RuleType type) {
        this.type = type;
    }

    public RuleType getType() {
        return type;
    }
}
