package com.psidnell;

import com.psidnell.rules.RuleSet;
import com.psidnell.words.Word;

public class Solver {
    //todo is this worth having?
    public static int calculateSolutions(Word dictionaryWordGuess, Word wordFromSolutions,
                                         RuleSet ruleSet,
                                         Solutions solutions) {
        int originalSize = solutions.getSolutions().size();
        RuleSet r = ruleSet.combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));
        int removed = solutions.countWordsNotMatching(r);
        return originalSize - removed;
    }
}
