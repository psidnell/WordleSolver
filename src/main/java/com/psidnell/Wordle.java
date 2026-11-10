package com.psidnell;

import com.psidnell.ruleset.RuleSet;
import com.psidnell.solution.SolutionEvaluator;
import com.psidnell.solution.Strategy;
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
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Wordle extends ComponentImpl {
    private static final Logger LOG = System.getLogger(Wordle.class.getName());

    private GameState state;

    // For info only
    private final List<Integer> userGuessSolutionsRemaining = new ArrayList<>();
    private final List<RuleSet> previousRuleSets = new ArrayList<>();

    private Word solution;

    @Override
    public void init() {
        super.init();
        this.state = new GameState(dictionary, new WordList(dictionary));
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
        System.out.println("Knowledge: " + state.getCombinedRuleSet().toString());
        System.out.println("Guesses Remaining: " + state.getGuessesRemaining());
        if (isSolved()) {
            System.out.println("Solved: " + state.getLastGuess());
        } else {
            System.out.println("Not Solved");
        }

        System.out.println("Solutions: " + state.getSolutions().getWords().size());
        if (state.getSolutions().getWords().size() <= 10) {
            System.out.print("Solutions Remaining:");
            System.out.println(state.getSolutions().getWords().stream().map(Word::toString).collect(Collectors.joining(",")));
        }
    }

    public boolean answer(String guessStr, String answer) {
        String guess = guessStr.toUpperCase();
        System.out.println("ANSWER:");
        Word guessWord = new Word(guess);
        RuleSet r = new RuleSet(guessWord, answer);
        int solutionsBefore = state.getSolutions().getWords().size();
        state.registerGuess(guessWord, r);

        int solutionsAfter = state.getSolutions().getWords().size();
        userGuessSolutionsRemaining.add(state.getSolutions().getWords().size());
        previousRuleSets.add(state.getCombinedRuleSet());

        System.out.println("Solutions dropped from " + solutionsBefore + " to " + solutionsAfter);

        if (state.getCombinedRuleSet().isSolved()) {
            System.out.println("Solved: " + guess);
            return false;
        }

        if (state.getGuessesRemaining() == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        if (state.getSolutions().getWords().isEmpty()) {
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
        int solutionsBefore = state.getSolutions().getWords().size();
        RuleSet r = new RuleSet(guessWord, solution);
        state.registerGuess(guessWord, r);

        LOG.log(Level.DEBUG, "Pre Cumulative: {0}", state.getCombinedRuleSet());

        userGuessSolutionsRemaining.add(state.getSolutions().getWords().size());
        previousRuleSets.add(state.getCombinedRuleSet());
        int solutionsAfter = state.getSolutions().getWords().size();

        LOG.log(Level.DEBUG, "Derived Match:   {0}", r);
        LOG.log(Level.DEBUG, "Post Cumulative: {0}", state.getCombinedRuleSet());
        LOG.log(Level.DEBUG, "Solutions dropped from: {0} to {1}", solutionsBefore, solutionsAfter);

        if (guessWord.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - state.getGuessesRemaining()) + " guesses");
            return true;
        }

        if (state.getSolutions().getWords().isEmpty()) {
            throw new IllegalArgumentException("Solutions are empty");
        }

        return false;
    }

    public String propose() {

        System.out.println("PROPOSE");

        if (state.getGuessesRemaining() == 6) {
            System.out.println("First Guess:" + Strategy.FIRST_GUESS);
            return Strategy.FIRST_GUESS;
        }

        int solutionCount = state.getSolutions().getWords().size();
        if (solutionCount == 1) {
            String lastWord = state.getSolutions().getWords().stream().findFirst().orElseThrow().getWord();
            System.out.println("Last Word:" + lastWord);
            return lastWord;
        }

        System.out.println("Solutions: " + solutionCount);

        // Choose the strategy and guess solutionCount based on the number of solutions remaining
        // to balance performance against optimal result. On this machine it
        // keeps it under 20s for the 2nd guess

        SolutionEvaluator evaluator = strategy.getOptimalStrategy(state);
        long startTime = System.currentTimeMillis();
        Word bestWord = evaluator.evaluateBestWord();
        long endTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (endTime - startTime) / 1000 + "s");

        return bestWord.getWord();
    }

    public int getGuessesRemaining() {
        return state.getGuessesRemaining();
    }

    public boolean isSolved() {
        if (solution == null) {
            return state.getCombinedRuleSet().isSolved();
        }

        if (state.getPreviousGuesses().isEmpty()) {
            return false;
        }

        return state.getLastGuess().equals(solution);
    }

    public void showRemaining() {

        TreeSet<Word> sorted = new TreeSet<>(state.getSolutions().getWords());

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

            for (Word wordFromSolutions : state.getSolutions().getWords()) {
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
