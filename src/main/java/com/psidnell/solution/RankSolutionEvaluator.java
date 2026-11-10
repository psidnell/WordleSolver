package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.words.Word;
import com.psidnell.words.WordComparison;

public class RankSolutionEvaluator implements SolutionEvaluator {
    private static final System.Logger LOG = System.getLogger(RankSolutionEvaluator.class.getName());

    private final GameState state;

    public RankSolutionEvaluator(GameState state) {
        this.state = state;
    }

    @Override
    public Word evaluateBestWord() {
        int totalCombinations = state.getDictionary().size() * state.getSolutions().size();
        LOG.log(System.Logger.Level.INFO, "Total solution/guess combinations to try: {0}", totalCombinations);
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

            LOG.log(System.Logger.Level.DEBUG, "Analysed: {0} Total Rank: {1}", dictionaryWordGuess, totalRankForWord);

            boolean dictWordInSolutions = state.getSolutions().contains(dictionaryWordGuess);
            boolean preferDictionaryWord = bestWord != null && dictWordInSolutions && !state.getSolutions().contains(bestWord);
            if (totalRankForWord >= highestRank && preferDictionaryWord) {
                LOG.log(System.Logger.Level.INFO, "Best (solution) word so far: {0} Rank: {1} {2}", dictionaryWordGuess, totalRankForWord, dictWordInSolutions);
                bestWord = dictionaryWordGuess;
                highestRank = totalRankForWord;
            } else if (totalRankForWord > highestRank) {
                LOG.log(System.Logger.Level.INFO, "Best word so far: {0} Rank: {1} {2}", dictionaryWordGuess, totalRankForWord, dictWordInSolutions);
                bestWord = dictionaryWordGuess;
                highestRank = totalRankForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                LOG.log(System.Logger.Level.DEBUG, "PercentDone: {0}%", percentDone);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.log(System.Logger.Level.INFO, "Best word: {0} Rank: {1}", bestWord, highestRank);
        return bestWord;
    }
}
