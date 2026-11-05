package com.psidnell;

import com.psidnell.rules.RuleSet;
import com.psidnell.util.ComponentImpl;
import com.psidnell.words.Word;
import com.psidnell.words.WordComparison;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Wordle extends ComponentImpl {
    private static final Logger LOG = System.getLogger(Wordle.class.getName());
    public static String FIRST_GUESS = "trace";
    private final List<Integer> userGuessSolutionsRemaining = new ArrayList<>();
    private final List<RuleSet> previousRuleSets = new ArrayList<>();
    private final List<String> previousGuesses = new ArrayList<>();
    private final Settings settings = Settings.getInstance();
    public int guessesMade = 0;
    int guessesRemaining = 6;
    private Word solution;
    private Solutions solutions;
    private RuleSet combinedRuleSet = new RuleSet();

    @Override
    public void init() {
        super.init();
        this.solutions = new Solutions(dictionary);
    }

    public void setSolution(String solutionStr) {
        this.solution = new Word(solutionStr);
    }

    public void printStatus() {
        System.out.println("STATUS:");
        if (solution != null) {
            System.out.println("Solution Known: " + solution.getWord());
        }
        System.out.println("Dictionary Size:" + dictionary.getWords().size());
        System.out.println("History:");
        for (int i = 0; i < previousRuleSets.size(); i++) {
            System.out.println("  - " + previousGuesses.get(i) + " " +
                    previousRuleSets.get(i) + " Solutions: " + userGuessSolutionsRemaining.get(i));
        }
        System.out.println("Knowledge: " + combinedRuleSet.toString());
        System.out.println("Guesses Remaining: " + guessesRemaining);
        if (isSolved()) {
            System.out.println("Solved: " + previousGuesses.get(previousGuesses.size() - 1));
        } else {
            System.out.println("Not Solved");
        }

        System.out.println("Solutions: " + solutions.getSolutions().size());
        if (solutions.getSolutions().size() <= 10) {
            System.out.print("Solutions Remaining:");
            System.out.println(solutions.getSolutions().stream().map(Word::toString).collect(Collectors.joining(",")));
        }
    }

    public boolean answer(String guessStr, String answer) {
        String guess = guessStr.toUpperCase();
        System.out.println("ANSWER:");
        Word guessWord = new Word(guess);
        RuleSet r = new RuleSet(guessWord, answer);
        combinedRuleSet = combinedRuleSet.combineRuleSet(r);

        int solutionsBefore = solutions.getSolutions().size();

        solutions.prune(combinedRuleSet);

        int solutionsAfter = solutions.getSolutions().size();

        previousGuesses.add(guess);
        guessesRemaining--;
        guessesMade++;
        userGuessSolutionsRemaining.add(solutions.getSolutions().size());
        previousRuleSets.add(combinedRuleSet);

        System.out.println("Solutions dropped from " + solutionsBefore + " to " + solutionsAfter);

        if (combinedRuleSet.isSolved()) {
            System.out.println("Solved: " + guess);
            return false;
        }

        if (guessesRemaining == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        if (solutions.getSolutions().isEmpty()) {
            throw new IllegalArgumentException("Cannot have no words left");
        }

        return true;
    }

    public boolean guess(String guessStr) {
        String guess = guessStr.toUpperCase();
        System.out.println("GUESS");
        System.out.println("Turn " + (7 - guessesRemaining) + ": Guessing " + guess);
        if (guessesRemaining == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        Word guessWord = new Word(guess);

        if (guess.length() != 5) {
            System.out.println("Guess must be 5 characters");
            return false;
        }

        if (!dictionary.hasWord(guess)) {
            System.out.println("Unknown word");
            return false;
        }

        // We have a valid guess
        previousGuesses.add(guess);
        guessesRemaining--;
        guessesMade++;

        LOG.log(Level.DEBUG, "Pre Cumulative: {0}", combinedRuleSet);

        int solutionsBefore = solutions.getSolutions().size();
        AtomicReference<RuleSet> rulesSoFarRef = new AtomicReference<>(combinedRuleSet);
        RuleSet match = new RuleSet(guessWord, solution);
        rulesSoFarRef.set(match.combineRuleSet(rulesSoFarRef.get()));
        solutions.prune(rulesSoFarRef.get());
        combinedRuleSet = rulesSoFarRef.get();
        userGuessSolutionsRemaining.add(solutions.getSolutions().size());
        previousRuleSets.add(combinedRuleSet);
        int solutionsAfter = solutions.getSolutions().size();

        LOG.log(Level.DEBUG, "Derived Match:   {0}", match);
        LOG.log(Level.DEBUG, "Post Cumulative: {0}", combinedRuleSet);
        LOG.log(Level.DEBUG, "Solutions dropped from: {0} to {1}", solutionsBefore, solutionsAfter);

        if (guessWord.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses");
            return true;
        }

        if (solutions.getSolutions().isEmpty()) {
            throw new IllegalArgumentException("Solutions are empty");
        }

        return false;
    }

    public String propose() {

        System.out.println("PROPOSE");

        if (guessesRemaining == 6) {
            System.out.println("First Guess:" + FIRST_GUESS);
            return FIRST_GUESS;
        }

        int solutionCount = solutions.getSolutions().size();
        if (solutionCount == 1) {
            String lastWord = solutions.getSolutions().stream().findFirst().orElseThrow().getWord();
            System.out.println("Last Word:" + lastWord);
            return lastWord;
        }

        System.out.println("Solutions: " + solutionCount);

        // Choose the strategy and guess solutionCount based on the number of solutions remaining
        // to balance performance against optimal result. On this machine it
        // keeps it under 20s for the 2nd guess
        Word bestWord;

        // Don't touch the higher levels when we provide a default first guess

        // Greater than: Uses solution 1
        final int l1 = settings.getSearchThresholdWorst();
        // Between: Uses solution 2
        final int l2 = settings.getSearchThresholdBest();
        // Below: Uses solution 3

        long startTime = System.currentTimeMillis();

        if (solutionCount > l1) { // Max to L1
            System.out.println("(1) Quick Search on reduced guesses above " + l1);
            Collection<Word> guesses = solutions.getSolutions();
            bestWord = getBestWordByRank(guesses);
        } else if (solutionCount > l2) {
            System.out.println("(2) Exhaustive Search on reduced guesses between " + l1 + " and " + l2);
            Collection<Word> guesses = solutions.getSolutions();
            bestWord = getBestWordByExhaustiveSearch(guesses);
        } else {
            System.out.println("(3) Exhaustive Search on maximum guesses below " + l2);
            Collection<Word> guesses = dictionary.getWords();
            bestWord = getBestWordByExhaustiveSearch(guesses);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (endTime - startTime) / 1000 + "s");

        return bestWord.getWord();
    }

    private Word getBestWordByExhaustiveSearch(Collection<Word> guesses) {
        LOG.log(Level.INFO, "Total solution/guess combinations to try: {0}", guesses.size());

        Word bestWord = null;
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        int lowestSolutionsCount = Integer.MAX_VALUE;

        for (Word dictionaryWordGuess : guesses) {
            if (previousGuesses.contains(dictionaryWordGuess.getWord())) {
                continue;
            }

            int totalSolutionsForWord = 0;

            for (Word wordFromSolutions : solutions.getSolutions()) {
                int originalSize = solutions.getSolutions().size();

                if (!dictionaryWordGuess.haveCommonCharacters(wordFromSolutions)) {
                    LOG.log(Level.DEBUG, "No common letters {0}/{1}", dictionaryWordGuess, wordFromSolutions);
                    totalSolutionsForWord += originalSize;
                    continue;
                }

                int originalSize1 = solutions.getSolutions().size();
                RuleSet r = combinedRuleSet.combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));
                int removed = solutions.countWordsNotMatching(r);
                int prunedSize = originalSize1 - removed;

                totalSolutionsForWord += prunedSize;

                if (totalSolutionsForWord > lowestSolutionsCount) {
                    // This guess is never going to be the best word - bail to save time
                    break;
                }

            }

            LOG.log(Level.DEBUG, "{0}", "Analysed: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);

            if (totalSolutionsForWord < lowestSolutionsCount) {
                LOG.log(Level.INFO, "Best word so far: {0} solutions: {1}", dictionaryWordGuess, totalSolutionsForWord);
                bestWord = dictionaryWordGuess;
                lowestSolutionsCount = totalSolutionsForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                LOG.log(Level.DEBUG, "PercentDone {0}%", percentDone);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.log(Level.INFO, "Best word: {0} Solutions", bestWord, lowestSolutionsCount);
        return bestWord;
    }

    private Word getBestWordByRank(Collection<Word> guesses) {
        LOG.log(Level.INFO, "Total solution/guess combinations to try: {0}", guesses.size());
        Word bestWord = null;
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        int highestRank = 0;

        for (Word dictionaryWordGuess : guesses) {
            if (previousGuesses.contains(dictionaryWordGuess.getWord())) {
                continue;
            }

            int totalRankForWord = 0;

            for (Word wordFromSolutions : solutions.getSolutions()) {

                WordComparison comparison = dictionaryWordGuess.compareWords(wordFromSolutions);
                totalRankForWord += comparison.charactersInCommon();
            }

            LOG.log(Level.DEBUG, "Analysed: {0} Total Rank: {1}", dictionaryWordGuess, totalRankForWord);

            if (totalRankForWord > highestRank) {
                LOG.log(Level.INFO, "Best word so far: {0} Rank: {1}", dictionaryWordGuess, totalRankForWord);
                bestWord = dictionaryWordGuess;
                highestRank = totalRankForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                LOG.log(Level.DEBUG, "PercentDone: {0}%", percentDone);
            }
        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        LOG.log(Level.INFO, "Best word: {0} Rank: {1}", bestWord, highestRank);
        return bestWord;
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    public boolean isSolved() {
        if (solution == null) {
            return combinedRuleSet.isSolved();
        }

        if (previousGuesses.isEmpty()) {
            return false;
        }

        return previousGuesses.getLast().equals(solution.getWord());
    }

    public void showRemaining() {
        for (Word word : solutions.getSolutions()) {
            System.out.println(word);
        }
    }
}
