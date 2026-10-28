package com.psidnell.rules;

import com.psidnell.Word;

public class Rule {
    private final RuleType type;
    private final Character ch;
    private final int position;

    public Rule(RuleType type, Character ch, int position) {
        this.type = type;
        this.ch = ch;
        this.position = position;
    }

    public boolean match(Word word) {
        return switch (type) {
            case MATCH -> word.charAt(position).equals(ch);
            case NO_MATCH -> !word.charAt(position).equals(ch);
            case WRONG_PLACE -> word.charAt(position) != ch && word.occurencesOf(ch) > 0;
        };
    }

    public RuleType getType () {
        return type;
    }

    public Character getCharacter () {
        return ch;
    }

    public int getPosition () {
        return position;
    }
}
