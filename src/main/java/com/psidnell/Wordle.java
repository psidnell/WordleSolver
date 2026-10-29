package com.psidnell;

import com.psidnell.rules.Rule;
import com.psidnell.rules.RuleSet;
import com.psidnell.util.ComponentImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Wordle extends ComponentImpl {

    private Word solution;
    int guessesRemaining = 6;
    private Solutions solutions;
    private final List<Integer> userGuessSolutionsRemaining = new ArrayList<>();
    private final List<RuleSet> previousRuleSets = new ArrayList<>();
    private RuleSet combinedRuleSet = new RuleSet();
    private final List<String> previousGuesses = new ArrayList<>();

    @Override
    public void init() {
        super.init();
        this.solutions = new Solutions(dictionary);
    }

    public void setSolution(String solution) {
        if (!dictionary.hasWord(solution)) {
            throw new IllegalArgumentException("solution is not a valid word");
        }
        this.solution = new Word (solution);
    }

    public void printStatus() {
        System.out.println("==============================");
        if (solution != null) {
            System.out.println("Solution Known: " + solution.getWord().toUpperCase());
        }
        System.out.println("Dictionary Size:" + dictionary.getWords().size());
        System.out.println("History:");
        for (int i = 0; i < previousRuleSets.size(); i++) {
            System.out.println("  - " + previousGuesses.get(i).toUpperCase() + " " +
                    previousRuleSets.get(i) + " " + userGuessSolutionsRemaining.get(i));
        }
        System.out.println("Knowledge: " + combinedRuleSet.toString());
        System.out.println("Guesses Remaining: " + guessesRemaining);
        if (isSolved()) {
            System.out.println("Solved: " + previousGuesses.get(previousGuesses.size() - 1).toUpperCase());
        } else {
            System.out.println("Not Solved");
        }

        System.out.println("Solutions: " + solutions.getSolutions().size());
        if (solutions.getSolutions().size() <= 10) {
            System.out.print("Solutions Remaining:");
            System.out.println(solutions.getSolutions().stream().map(Word::toString).collect(Collectors.joining(",")));
        }
        System.out.println("==============================");

    }

    // TODO Move all of the tests snd printing here
    public boolean userGuess(String guess) {
        System.out.println("*******************************");
        try {
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

            System.out.println("Pre Cumulative: " + combinedRuleSet);

            int solutionsBefore = solutions.getSolutions().size();
            AtomicReference<RuleSet> rulesSoFarRef = new AtomicReference<>(combinedRuleSet);
            RuleSet match = guess(guessWord, solution, solutions, rulesSoFarRef);
            combinedRuleSet = rulesSoFarRef.get();
            userGuessSolutionsRemaining.add(solutions.getSolutions().size());
            previousRuleSets.add(combinedRuleSet);
            int solutionsAfter = solutions.getSolutions().size();

            System.out.println("Derived Match:   " + match);
            System.out.println("Post Cumulative: " + combinedRuleSet);
            System.out.println("Solutions dropped from " + solutionsBefore + " to " + solutionsAfter);


            if (guessWord.equals(solution)) {
                System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses");

                return true;
            }

            if (solutions.getSolutions().isEmpty()) {
                throw new IllegalArgumentException("Solutions are empty");
            }

            return false;
        } finally {
            System.out.println("*******************************");
        }
    }



    public void showRemaining() {
        for (Word word : solutions.getSolutions()) {
            System.out.println(word);
        }
    }

    public Solutions hypothesize (Word guessWord, Word solution, RuleSet rulesSoFar) {
        Solutions reducedSolutions = new Solutions(solutions);
        RuleSet m = guess(guessWord, solution, reducedSolutions, new AtomicReference<>(rulesSoFar));
        return reducedSolutions;
    }

    public RuleSet guess(Word guess, Word solution, Solutions solutions, AtomicReference<RuleSet> rulesSoFarRef) {
        RuleSet match = new RuleSet(guess, solution);
        rulesSoFarRef.set(match.combineRuleSet(rulesSoFarRef.get()));
        solutions.prune(rulesSoFarRef.get());
        return match;
    }

    public String propose() {
        // Try every dictionary word against all the remaining words and see which
        // best reduces the solution size

        if (solutions.getSolutions().size() == 1) {
            return solutions.getSolutions().stream().findFirst().orElseThrow().getWord();
        }

        Word bestWord = null;
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;

        int lowestSolutionsCount = Integer.MAX_VALUE;

        for (Word dictionaryWordGuess: dictionary.getWords().values()) {
            if (previousGuesses.contains(dictionaryWordGuess.getWord())) {
                continue;
            }

            int totalSolutionsForWord = 0;

            for (Word wordFromSolutions : solutions.getSolutions()) {

                RuleSet r = combinedRuleSet.combineRuleSet(new RuleSet(dictionaryWordGuess, wordFromSolutions));

                int originalSize = solutions.getSolutions().size();
                Solutions tmpSolutions = new Solutions(solutions);
                tmpSolutions.prune(r);
                int prunedSize = tmpSolutions.getSolutions().size();
                //int reduction = originalSize - prunedSize;
                //System.out.println("Analysed: " + dictionaryWordGuess + "/" + wordFromSolutions + " reduction:" + reduction);
                totalSolutionsForWord += prunedSize;

                if (totalSolutionsForWord > lowestSolutionsCount) {
                    // This is never going to be the best word - bail to save time
                    break;
                }
            }

            //System.out.println("Analysed: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);

            if (totalSolutionsForWord < lowestSolutionsCount) {
                System.out.println("Best word so far: " + dictionaryWordGuess + " solutions: " + totalSolutionsForWord);
                bestWord = dictionaryWordGuess;
                lowestSolutionsCount = totalSolutionsForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                //System.out.println("PercentDone: " + percentDone);
            }

        }

        if (bestWord == null) {
            throw new IllegalStateException("No best word found");
        }

        System.out.println("Best word: " + bestWord + " solutions: " + lowestSolutionsCount);
        return bestWord.getWord();
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    public Solutions getSolutions() {
        return solutions;
    }

    public boolean isSolved() {
        return
                solution != null &&
                !previousGuesses.isEmpty() &&
                previousGuesses.getLast().equals(solution.getWord());
    }
}
