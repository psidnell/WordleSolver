package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

public interface Rule {

    RuleType getType();

    <T extends Rule> T combine(T r2);

    Rule copy();

    boolean match(Word word);
}
