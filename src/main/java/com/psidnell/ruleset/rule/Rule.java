package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;
import java.util.Set;

public interface Rule {

    Rule combine(Rule r2);

    RuleType getType();

    int getPosition();

    //String getCharactersAsString();

    int getAmount();

    Set<Character> getCharacters();

    boolean match(Word word);
}
