package com.psidnell.rules;

import java.util.Objects;

public abstract class AbstractRule implements Rule {
    protected final RuleType type;

    protected AbstractRule(RuleType type) {
        this.type = type;
    }

    public RuleType getType() {
        return type;
    }

    @Override
    public int compareTo(Rule o) {
        int result;

        if (getPosition() == -1 && o.getPosition() != -1) {
            return 1;
        }

        if (getPosition() != -1 && o.getPosition() == -1) {
            return -1;
        }

        result = Integer.compare(getPosition(), o.getPosition());
        if (result != 0) {
            return result;
        }

        result = getType().compareTo(o.getType());
        if (result != 0) {
            return result;
        }

        result = getCharactersAsString().compareTo(o.getCharactersAsString());
        if (result != 0) {
            return result;
        }

        return Integer.compare(getAmount(), o.getAmount());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuleImpl rule = (RuleImpl) o;
        return getPosition() == rule.getPosition() && getAmount() == rule.getAmount() && getType() == rule.getType() && Objects.equals(getCharactersAsString(), rule.getCharactersAsString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getCharactersAsString(), getPosition(), getAmount());
    }
}
