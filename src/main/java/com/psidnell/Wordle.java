package com.psidnell;

import com.psidnell.ruleset.RuleSet;
import com.psidnell.util.ComponentImpl;
import com.psidnell.util.Pair;
import com.psidnell.words.Word;
import com.psidnell.words.WordComparison;
import com.psidnell.words.WordList;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Wordle extends ComponentImpl {
    private static final Logger LOG = System.getLogger(Wordle.class.getName());
    public static String FIRST_GUESS = "trace";

    private GameState state;

    // For info only
    private final List<Integer> userGuessSolutionsRemaining = new ArrayList<>();
    private final List<RuleSet> previousRuleSets = new ArrayList<>();

    private final Settings settings = Settings.getInstance();

    private Word solution;
    private RuleSet combinedRuleSet = new RuleSet();
    private WordList solutions;

    @Override
    public void init() {
        super.init();
        this.state = new GameState(dictionary, dictionary);
        this.solutions = new WordList(dictionary);
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
            System.out.println("  - " + state.getPreviousGuesses().get(i) + " " +
                    previousRuleSets.get(i) + " Solutions: " + userGuessSolutionsRemaining.get(i));
        }
        System.out.println("Knowledge: " + combinedRuleSet.toString());
        System.out.println("Guesses Remaining: " + state.getGuessesRemaining());
        if (isSolved()) {
            System.out.println("Solved: " + state.getLastGuess());
        } else {
            System.out.println("Not Solved");
        }

        System.out.println("Solutions: " + solutions.getWords().size());
        if (solutions.getWords().size() <= 10) {
            System.out.print("Solutions Remaining:");
            System.out.println(solutions.getWords().stream().map(Word::toString).collect(Collectors.joining(",")));
        }
    }

    public boolean answer(String guessStr, String answer) {
        String guess = guessStr.toUpperCase();
        System.out.println("ANSWER:");
        Word guessWord = new Word(guess);
        RuleSet r = new RuleSet(guessWord, answer);
        combinedRuleSet = combinedRuleSet.combineRuleSet(r);

        int solutionsBefore = solutions.getWords().size();

        solutions.prune(combinedRuleSet);

        int solutionsAfter = solutions.getWords().size();

        state.registerGuess(guessWord);
        userGuessSolutionsRemaining.add(solutions.getWords().size());
        previousRuleSets.add(combinedRuleSet);

        System.out.println("Solutions dropped from " + solutionsBefore + " to " + solutionsAfter);

        if (combinedRuleSet.isSolved()) {
            System.out.println("Solved: " + guess);
            return false;
        }

        if (state.getGuessesRemaining() == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        if (solutions.getWords().isEmpty()) {
            throw new IllegalArgumentException("Cannot have no words left");
        }

        return true;
    }

    public boolean guess(String guessStr) {
        String guess = guessStr.toUpperCase();
        System.out.println("GUESS");
        System.out.println("Turn " + (7 - state.getGuessesRemaining()) + ": Guessing " + guess);
        if (state.getGuessesRemaining() == 0) {
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
        state.registerGuess(guessWord);

        LOG.log(Level.DEBUG, "Pre Cumulative: {0}", combinedRuleSet);

        // TODO ugly!
        int solutionsBefore = solutions.getWords().size();
        AtomicReference<RuleSet> rulesSoFarRef = new AtomicReference<>(combinedRuleSet);
        RuleSet match = new RuleSet(guessWord, solution);
        rulesSoFarRef.set(match.combineRuleSet(rulesSoFarRef.get()));
        solutions.prune(rulesSoFarRef.get());
        combinedRuleSet = rulesSoFarRef.get();
        userGuessSolutionsRemaining.add(solutions.getWords().size());
        previousRuleSets.add(combinedRuleSet);
        int solutionsAfter = solutions.getWords().size();

        LOG.log(Level.DEBUG, "Derived Match:   {0}", match);
        LOG.log(Level.DEBUG, "Post Cumulative: {0}", combinedRuleSet);
        LOG.log(Level.DEBUG, "Solutions dropped from: {0} to {1}", solutionsBefore, solutionsAfter);

        if (guessWord.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - state.getGuessesRemaining()) + " guesses");
            return true;
        }

        if (solutions.getWords().isEmpty()) {
            throw new IllegalArgumentException("Solutions are empty");
        }

        return false;
    }

    public String propose() {

        System.out.println("PROPOSE");

        if (state.getGuessesRemaining() == 6) {
            System.out.println("First Guess:" + FIRST_GUESS);
            return FIRST_GUESS;
        }

        int solutionCount = solutions.getWords().size();
        if (solutionCount == 1) {
            String lastWord = solutions.getWords().stream().findFirst().orElseThrow().getWord();
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
            Collection<Word> guesses = solutions.getWords();
            bestWord = getBestWordByRank(guesses);
        } else if (solutionCount > l2) {
            System.out.println("(2) Exhaustive Search on reduced guesses between " + l2 + " and " + l1);
            Collection<Word> guesses = solutions.getWords();
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
        int totalCombinations = guesses.size() * solutions.size();
        LOG.log(Level.INFO, "Total solution/guess combinations to try: {0}", totalCombinations);

        Word bestWord = null;
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        int shortcuts = 0;
        int lowestSolutionsCount = Integer.MAX_VALUE;

        for (Word dictionaryWordGuess : guesses) {
            if (state.haveAlreadyGuessed(dictionaryWordGuess)) {
                continue;
            }

            int totalSolutionsForWord = 0;

            for (Word wordFromSolutions : solutions.getWords()) {
                int originalSize = solutions.getWords().size();

                RuleSet r = combinedRuleSet.combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));
                int removed = solutions.countWordsNotMatching(r);
                int prunedSize = originalSize - removed;

                totalSolutionsForWord += prunedSize;

                if (totalSolutionsForWord > lowestSolutionsCount) {
                    // This guess is never going to be the best word - bail to save time
                    shortcuts++;
                    break;
                }
            }

            LOG.log(Level.DEBUG, "{0}", "Analysed: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);

            boolean dictWordInSolutions = solutions.contains(dictionaryWordGuess);
            boolean preferDictionaryWord = bestWord != null && dictWordInSolutions && !solutions.contains(bestWord);
            if (totalSolutionsForWord <= lowestSolutionsCount && preferDictionaryWord) {
                // Prefer a guess if it's in the solution set
                LOG.log(Level.INFO, "Best (solution) word so far: {0} solutions: {1} {2} {3} ", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
                bestWord = dictionaryWordGuess;
                lowestSolutionsCount = totalSolutionsForWord;
            } else if (totalSolutionsForWord < lowestSolutionsCount) {
                LOG.log(Level.INFO, "Best word so far: {0} solutions: {1} {2} {3}", dictionaryWordGuess, totalSolutionsForWord, dictWordInSolutions, shortcuts);
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

        LOG.log(Level.INFO, "Best word: {0} Solutions {1} Shortcuts {2}", bestWord, lowestSolutionsCount, shortcuts);
        return bestWord;
    }

    private Word getBestWordByRecursion(Collection<Word> guesses) {
        return recurse(guesses, solutions, combinedRuleSet, state.getGuessesRemaining()).left();
    }

    private Pair<Word, Integer> recurse(Collection<Word> guesses, WordList solutions, RuleSet r, int guessesRemaining) {
        LOG.log(Level.INFO, "guessesRem {0} guesses {1} solutions {2}", guessesRemaining, guesses.size(), solutions.size());

        if (solutions.size() == 1) {
            return new Pair<>(solutions.getWords().stream().findFirst().orElseThrow(), 1);
        }

        if (guessesRemaining == 0) {
            return null;
        }

        Word bestWord = null;
        int bestDepths = Integer.MAX_VALUE;

        for (Word guess : guesses) {
            int totalDepthsForGuess = 0;
            Set<Word> reducedGuesses = new TreeSet<>(guesses);
            reducedGuesses.remove(guess);
            for (Word wordFromSolutions : solutions.getWords()) {
                LOG.log(Level.INFO, "{0}/{1}", guess, wordFromSolutions);
                WordList reducedSolutions = new WordList(solutions);
                RuleSet r2 = r.combineRuleSet(new RuleSet(guess, wordFromSolutions));
                reducedSolutions.prune(r2);
                Pair<Word, Integer> p = recurse(reducedGuesses, reducedSolutions, r2, guessesRemaining - 1);
                if (p != null) {
                    totalDepthsForGuess += p.right();
                }
            }
            if (totalDepthsForGuess < bestDepths) {
                bestWord = guess;
            }
        }
        return new Pair<>(bestWord, bestDepths);
    }

    private Word getBestWordByRank(Collection<Word> guesses) {
        int totalCombinations = guesses.size() * solutions.size();
        LOG.log(Level.INFO, "Total solution/guess combinations to try: {0}", totalCombinations);
        Word bestWord = null;
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        int highestRank = 0;

        for (Word dictionaryWordGuess : guesses) {
            if (state.getPreviousGuesses().contains(dictionaryWordGuess.getWord())) {
                continue;
            }

            int totalRankForWord = 0;

            for (Word wordFromSolutions : solutions.getWords()) {
                WordComparison comparison = dictionaryWordGuess.compareWords(wordFromSolutions);
                totalRankForWord += comparison.getRank();
            }

            LOG.log(Level.DEBUG, "Analysed: {0} Total Rank: {1}", dictionaryWordGuess, totalRankForWord);

            boolean dictWordInSolutions = solutions.contains(dictionaryWordGuess);
            boolean preferDictionaryWord = bestWord != null && dictWordInSolutions && !solutions.contains(bestWord);
            if (totalRankForWord >= highestRank && preferDictionaryWord) {
                LOG.log(Level.INFO, "Best (solution) word so far: {0} Rank: {1} {2}", dictionaryWordGuess, totalRankForWord, dictWordInSolutions);
                bestWord = dictionaryWordGuess;
                highestRank = totalRankForWord;
            } else if (totalRankForWord > highestRank) {
                LOG.log(Level.INFO, "Best word so far: {0} Rank: {1} {2}", dictionaryWordGuess, totalRankForWord, dictWordInSolutions);
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
        return state.getGuessesRemaining();
    }

    public boolean isSolved() {
        if (solution == null) {
            return combinedRuleSet.isSolved();
        }

        if (state.getPreviousGuesses().isEmpty()) {
            return false;
        }

        return state.getLastGuess().equals(solution);
    }

    public void showRemaining() {

        TreeSet<Word> sorted = new TreeSet<>(solutions.getWords());

        for (Word word : sorted) {
            System.out.println(word);
        }
    }

    // TODO Needed? A possible optimisation
    private WordList shrinkWordsByRank(Collection<Word> guesses, int limit) {
        LOG.log(Level.INFO, "Shrinking words: {0}", guesses.size());

        ArrayList<Pair<Word, Integer>> collectedWordsAndRanks = new ArrayList<>();

        for (Word dictionaryWordGuess : guesses) {

            int totalRankForWord = 0;

            for (Word wordFromSolutions : solutions.getWords()) {
                WordComparison comparison = dictionaryWordGuess.compareWords(wordFromSolutions);
                totalRankForWord += comparison.charactersInCommon();
            }

            collectedWordsAndRanks.add(new Pair<>(dictionaryWordGuess, totalRankForWord));
        }

        collectedWordsAndRanks.sort((a, b) ->Integer.compare(b.right(), a.right()));
        List<Pair<Word, Integer>> truncatedWordsAndRanks = collectedWordsAndRanks.stream().limit(limit).toList();
        List<Word> truncatedWords = truncatedWordsAndRanks.stream().map(Pair::left).toList();
        LOG.log(Level.INFO, "Shrunk words: {0}", truncatedWords.size());
        return new WordList(truncatedWords);
    }

    public GameState getState() {
        return state;
    }
}
