package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.words.Word;
import com.psidnell.words.WordComparison;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RankSolutionEvaluator implements SolutionEvaluator {
    private static final Logger LOG = LoggerFactory.getLogger(RankSolutionEvaluator.class);

    private final GameState state;

    public RankSolutionEvaluator(GameState state) {
        this.state = state;
    }

    @Override
    public Word evaluateBestWord() {
        int totalCombinations = state.getDictionary().size() * state.getSolutions().size();
        LOG.info("Total solution/guess combinations to try: {}", totalCombinations);
        Word bestWord = null;
        int dictWords = state.getDictionary().getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        int highestRank = 0;

        for (Word dictionaryWordGuess : state.getDictionary().getWords()) {
            if (state.getPreviousGuesses().contains(dictionaryWordGuess.getWord())) {
                continue;
            }

            int totalRankForWord = 0;

            for (Word wordFromSolutions : state.getSolutions().getWords()) {
                WordComparison comparison = dictionaryWordGuess.compareWords(wordFromSolutions);
                totalRankForWord += comparison.getRank();
            }

            LOG.debug("Analysed: {} Total Rank: {}", dictionaryWordGuess, totalRankForWord);

            boolean dictWordInSolutions = state.getSolutions().contains(dictionaryWordGuess);
            boolean preferDictionaryWord = bestWord != null && dictWordInSolutions && !state.getSolutions().contains(bestWord);
            if (totalRankForWord >= highestRank && preferDictionaryWord) {
                LOG.info("Best (solution) word so far: {} Rank: {} {}", dictionaryWordGuess, totalRankForWord, dictWordInSolutions);
                bestWord = dictionaryWordGuess;
                highestRank = totalRankForWord;
            } else if (totalRankForWord > highestRank) {
                LOG.info("Best word so far: {} Rank: {} {}", dictionaryWordGuess, totalRankForWord, dictWordInSolutions);
                bestWord = dictionaryWordGuess;
                highestRank = totalRankForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                LOG.debug("PercentDone: {}%", percentDone);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.info("Best word: {} Rank: {}", bestWord, highestRank);
        return bestWord;
    }
}
