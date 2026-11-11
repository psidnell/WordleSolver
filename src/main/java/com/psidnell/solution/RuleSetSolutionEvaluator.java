package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.ruleset.RuleSet;
import com.psidnell.words.Word;
import com.psidnell.words.WordList;

import java.util.LinkedList;


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

        LinkedList<Word> prioritisedDictionary = new LinkedList<>(state.getSolutions().getWords());
        state.getDictionary().getWords().forEach(w -> {
            if (!state.getSolutions().contains(w)) {
                prioritisedDictionary.add(w);
            }
        });
        LOG.log(System.Logger.Level.INFO, "Prioritised: {0}", prioritisedDictionary.size());

        for (Word dictionaryWordGuess : prioritisedDictionary) {
            if (state.haveAlreadyGuessed(dictionaryWordGuess)) {
                continue;
            }

            int totalSolutionsForWord = 0;

            for (Word wordFromSolutions : state.getSolutions().getWords()) {
                RuleSet r = state.getCombinedRuleSet().combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));
                totalSolutionsForWord = countWordsMatching(state.getSolutions(), r, totalSolutionsForWord, lowestSolutionsCount);
                if (totalSolutionsForWord > lowestSolutionsCount) {
                    // We bailed after hitting the threshold
                    shortcuts++;
                    break;
                }
            }

            LOG.log(System.Logger.Level.DEBUG, "{0}", "Analysed: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);

            if (totalSolutionsForWord <= lowestSolutionsCount) {
                boolean dictWordInSolutions = state.getSolutions().contains(dictionaryWordGuess);
                boolean bestWordInSolutions = bestWord != null && state.getSolutions().contains(bestWord);

                boolean preferDictionaryWord = dictWordInSolutions && !bestWordInSolutions;

                if (preferDictionaryWord) {
                    // Prefer a guess if it's in the solution set
                    LOG.log(System.Logger.Level.INFO, "Best (solution) word so far: {0} solutions: {1} {2} {3} ", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                    bestWord = dictionaryWordGuess;
                } else if (totalSolutionsForWord < lowestSolutionsCount) {
                    LOG.log(System.Logger.Level.INFO, "Best word so far: {0} solutions: {1} {2} {3}", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                    bestWord = dictionaryWordGuess;
                }

                lowestSolutionsCount = totalSolutionsForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                LOG.log(System.Logger.Level.INFO, "PercentDone {0}% ({1}, {2})", percentDone, dictionaryWordGuess, totalSolutionsForWord);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.log(System.Logger.Level.INFO, "Best word: {0} Solutions {1} Shortcuts {2}", bestWord, lowestSolutionsCount, shortcuts);
        return bestWord;
    }

    public int countWordsMatching(WordList words, RuleSet match, int count, int threshold) {
        for (Word word : words.getWords()) {
            if (match.match(word)) {
                count++;
                if (count > threshold) {
                    // This guess is never going to be the best word - bail to save time
                    return count;
                }
            }
        }
        return count;
    }
}
