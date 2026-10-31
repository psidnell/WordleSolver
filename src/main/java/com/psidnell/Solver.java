package com.psidnell;

import com.psidnell.rules.RuleSet;

public class Solver {
    public static int calculateRank(Word dictionaryWordGuess, Word wordFromSolutions,
                                    RuleSet ruleSet,
                                    Solutions solutions) {
        int originalSize = solutions.getSolutions().size();
        RuleSet r = ruleSet.combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));
        int removed = solutions.countWordsNotMatching(r);
        return originalSize - removed;
    }

    public static int calculateRank2(RuleSet ruleSet, Solutions solutions) {
        int originalSize = solutions.getSolutions().size();
        int removed = solutions.countWordsNotMatching(ruleSet);
        return originalSize - removed;
    }
}
