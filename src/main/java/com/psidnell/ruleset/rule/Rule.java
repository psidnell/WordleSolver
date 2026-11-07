package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

public interface Rule {

    RuleType getType();

    Rule combine(Rule r2);

    boolean match(Word word);
}
