package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.Main;
import com.psidnell.ruleset.RuleSet;
import com.psidnell.words.Word;
import com.psidnell.words.WordList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


public class RuleSetSolutionEvaluator implements SolutionEvaluator {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private final GameState state;

    public RuleSetSolutionEvaluator(GameState state) {
        this.state = state;
    }

    @Override
    public Word evaluateBestWord() {
        int totalCombinations = state.getDictionary().size() * state.getSolutions().size();
        LOG.info("Total solution/guess combinations to try: {}", totalCombinations);

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
        LOG.info("Prioritised: {}", prioritisedDictionary.size());

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

            LOG.debug("{}", "Analysed: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);

            if (totalSolutionsForWord <= lowestSolutionsCount) {
                boolean dictWordInSolutions = state.getSolutions().contains(dictionaryWordGuess);
                boolean bestWordInSolutions = bestWord != null && state.getSolutions().contains(bestWord);

                boolean preferDictionaryWord = dictWordInSolutions && !bestWordInSolutions;

                if (preferDictionaryWord) {
                    // Prefer a guess if it's in the solution set
                    LOG.info("Best (solution) word so far: {} solutions: {} {} {} ", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                    bestWord = dictionaryWordGuess;
                } else if (totalSolutionsForWord < lowestSolutionsCount) {
                    LOG.info("Best word so far: {} solutions: {} {} {}", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                    bestWord = dictionaryWordGuess;
                }

                lowestSolutionsCount = totalSolutionsForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                //LOG.log(System.Logger.Level.INFO, "PercentDone {}% ({}, {})", percentDone, dictionaryWordGuess, totalSolutionsForWord);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.info("Best word: {} Solutions {} Shortcuts {}", bestWord, lowestSolutionsCount, shortcuts);
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
