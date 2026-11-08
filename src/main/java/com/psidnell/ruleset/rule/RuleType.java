package com.psidnell.ruleset.rule;

public enum RuleType {

    /**
     * Single character is in the correct position.
     */
    MATCH,

    /**
     * Position cannot have any of these characters.
     */
    NOT,

    /**
     * Position cannot have any of these characters, but they do exist in the word.
     */
    WRONG_PLACE,

    /**
     * The word cannot have any of these characters.
     */
    MUST_NOT_HAVE,

    /**
     * The word must have at least this many of the character.
     */
    MUST_HAVE_AT_LEAST,

    /**
     * The word must have at most this many of the character.
     */
    MUST_HAVE_AT_MOST
}
