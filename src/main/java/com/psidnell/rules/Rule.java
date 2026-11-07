package com.psidnell.rules;

import com.psidnell.words.Word;
import java.util.Set;

public interface Rule extends Comparable<Rule> {

    Rule combine(Rule r2);

    RuleType getType();

    int getPosition();

    String getCharactersAsString();

    int getAmount();

    Set<Character> getCharacters();

    boolean match(Word word);

    static Rule createMatch(Character ch, int position) {
        return new MatchRule(ch, position);
    }

    // TODO Inline all these
    static Rule createNot(Character ch, int position) {
        return new NotRule(ch, position);
    }

    static Rule createWrongPlace(Character ch, int position) {
        return new WrongPlaceRule(ch, position);
    }

    static Rule createMustNotHave(Character ch) {
        return new RuleImpl(RuleType.MUST_NOT_HAVE, ch, -1, -1);
    }

    static Rule createMustHaveAtLeast(Character ch, int amount) {
        return new MustHaveAtLeastRule(ch, amount);
    }

    static Rule createMustHaveAtMost(Character ch, int amount) {
        return new RuleImpl(RuleType.MUST_HAVE_AT_MOST, ch, -1, amount);
    }
}
