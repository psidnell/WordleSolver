package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.ruleset.RuleSet;
import com.psidnell.words.Word;

/**
 * This (or something like it) looks like it could work, but the time/memory requirements are exponential.
 */
public class RuleSetSolutionEvaluator implements SolutionEvaluator {
    private static final System.Logger LOG = System.getLogger(RuleSetSolutionEvaluator.class.getName());

    private final GameState state;

    public RuleSetSolutionEvaluator(GameState state) {
        this.state = state;
    }

    @Override
    public Word evaluateBestWord() {
        int totalCombinations = state.getDictionary().size() * state.getSolutions().size();
        LOG.log(System.Logger.Level.INFO, "Total solution/guess combinations to try: {0}", totalCombinations);

        Word bestWord = null;
        int dictWords = state.getDictionary().size();
        int wordsDone = 0;
        int percentDone = -1;
        int shortcuts = 0;
        int lowestSolutionsCount = Integer.MAX_VALUE;

        for (Word dictionaryWordGuess : state.getDictionary().getWords()) {
            if (state.haveAlreadyGuessed(dictionaryWordGuess)) {
                continue;
            }

            int totalSolutionsForWord = 0;

            for (Word wordFromSolutions : state.getSolutions().getWords()) {
                int originalSize = state.getSolutions().getWords().size();

                RuleSet r = state.getCombinedRuleSet().combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));
                int removed = state.getSolutions().countWordsNotMatching(r);
                int prunedSize = originalSize - removed;

                totalSolutionsForWord += prunedSize;

                if (totalSolutionsForWord > lowestSolutionsCount) {
                    // This guess is never going to be the best word - bail to save time
                    shortcuts++;
                    break;
                }
            }

            LOG.log(System.Logger.Level.DEBUG, "{0}", "Analysed: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);

            boolean dictWordInSolutions = state.getSolutions().contains(dictionaryWordGuess);

            boolean preferDictionaryWord = bestWord != null &&
                    dictWordInSolutions &&
                    !state.getSolutions().contains(bestWord);

            if (totalSolutionsForWord <= lowestSolutionsCount && preferDictionaryWord) {
                // Prefer a guess if it's in the solution set
                LOG.log(System.Logger.Level.INFO, "Best (solution) word so far: {0} solutions: {1} {2} {3} ", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                bestWord = dictionaryWordGuess;
                lowestSolutionsCount = totalSolutionsForWord;
            } else if (totalSolutionsForWord < lowestSolutionsCount) {
                LOG.log(System.Logger.Level.INFO, "Best word so far: {0} solutions: {1} {2} {3}", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                bestWord = dictionaryWordGuess;
                lowestSolutionsCount = totalSolutionsForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                LOG.log(System.Logger.Level.DEBUG, "PercentDone {0}%", percentDone);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.log(System.Logger.Level.INFO, "Best word: {0} Solutions {1} Shortcuts {2}", bestWord, lowestSolutionsCount, shortcuts);
        return bestWord;
    }
}
