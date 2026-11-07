package com.psidnell.ruleset.rule;

import com.psidnell.words.Word;

import java.util.Set;
import java.util.TreeSet;

public class MustNotHaveRule extends AbstractRule {
    private final TreeSet<Character> chars;

    public MustNotHaveRule(Character ch) {
        TreeSet<Character> chars = new TreeSet<>();
        chars.add(ch);
        this(chars);
    }

    public MustNotHaveRule(TreeSet<Character> chars) {
        super(RuleType.MUST_NOT_HAVE);
        this.chars = chars;
    }

    @Override
    public Rule combine(Rule o) {
        MustNotHaveRule other = (MustNotHaveRule) o;

        TreeSet<Character> combinedChars = new TreeSet<>(getCharacters());
        combinedChars.addAll(other.getCharacters());
        return new MustNotHaveRule(combinedChars);
    }

    @Override
    public boolean match(Word word) {
        for (Character ch : getCharacters()) {
            boolean mustNotHaveMatch = word.occurrencesOf(ch) == 0;
            if (!mustNotHaveMatch) {
                return false;
            }
        }
        return true;
    }

    public Set<Character> getCharacters() {
        return chars;
    }

    @Override
    public String toString() {
        return charactersToString(chars) + "!";
    }
}
