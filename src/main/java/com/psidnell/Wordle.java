package com.psidnell;

import com.psidnell.rules.Rule;
import com.psidnell.rules.RuleSet;
import com.psidnell.util.ComponentImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Wordle extends ComponentImpl {

    private Word solution;
    int guessesRemaining = 6;
    private Solutions solutions;
    private final List<Integer> userGuessSolutionsRemaining = new ArrayList<>();
    private final List<RuleSet> previousRuleSets = new ArrayList<>();
    private RuleSet combinedRuleSet = new RuleSet();
    private final List<String> previousGuesses = new ArrayList<>();
    private final List<String> previousReconstructions = new ArrayList<>();


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
        if (solution != null) {
            System.out.println("Solution Known: " + solution.getWord().toUpperCase());
        }
        System.out.println("Dictionary Size:" + dictionary.getWords().size());
        System.out.println("History:");
        for(int i = 0; i < previousRuleSets.size(); i++) {
            System.out.println("  - " + previousGuesses.get(i).toUpperCase() + " " +
                    previousReconstructions.get(i) + " " +
                    previousRuleSets.get(i) + " " + userGuessSolutionsRemaining.get(i));
        }
        System.out.println("Knowledge: " + combinedRuleSet.toString());
        System.out.println("Solutions: " + solutions.getSolutions().size());
        System.out.println("Guesses Remaining: " + guessesRemaining);
        if (isSolved()) {
            System.out.println("Solved: " + previousGuesses.get(previousGuesses.size() - 1).toUpperCase());
        } else {
            System.out.println("Not Solved");
        }
    }

    // TODO Move all of the tests snd printing here
    public boolean userGuess(String guess) {

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

        AtomicReference<RuleSet> rulesSoFarRef = new AtomicReference<>(combinedRuleSet);
        RuleSet match = guess(guessWord, solution, solutions, rulesSoFarRef);
        combinedRuleSet = rulesSoFarRef.get();
        previousReconstructions.add(combinedRuleSet.reconstructWordFromRules());

        userGuessSolutionsRemaining.add(solutions.getSolutions().size());
        previousRuleSets.add(match);

        System.out.println(match);


        if (guessWord.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses" );

            return true;
        }

        return false;
    }

    public RuleSet guess(Word guess, Word solution, Solutions solutions, AtomicReference<RuleSet> rulesSoFarRef) {
        RuleSet match = new RuleSet(guess, solution);
        rulesSoFarRef.set(match.combineRuleSet(rulesSoFarRef.get()));
        solutions.prune(rulesSoFarRef.get());
        return match;
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

    public String propose() {
        // Create a new wordle instance and guess with every dictionary word
        // Remember the one with the smallest resultant solutions number

        // Shouldn't be necessary
        if (solutions.getSolutions().size() == 1) {
            return solutions.getSolutions().stream().findFirst().get().getWord();
        }

        Word bestWord = null;
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;

        int bestUncertainty = Integer.MAX_VALUE;

        for (Word dictionaryWord: dictionary.getWords().values()) {
            if (previousGuesses.contains(dictionaryWord.getWord())) {
                continue;
            }

            int totalUncertaintyForWord = 0;

            for (Word wordFromSolutions : solutions.getSolutions()) {

                RuleSet ruleSet = new RuleSet(dictionaryWord, wordFromSolutions).combineRuleSet(combinedRuleSet);

                int uncertainty = ruleSet.getUncertainty();
                totalUncertaintyForWord += uncertainty;

                //System.out.println("Uncertainty: " + dictionaryWord + "/" + wordFromSolutions + " with " + uncertainty);


                if (totalUncertaintyForWord >= bestUncertainty) {
                    // System.out.println("Bailing on: " + dictionaryWord + " uncertainty: " + totalUncertaintyForWord);
                    break;
                }
            }

            //System.out.println("Analysed: " + dictionaryWord + " solutions: " + totalUncertaintyForWord);

            if (totalUncertaintyForWord < bestUncertainty) {
                System.out.println("Best word so far: " + dictionaryWord + " uncertainty: " + totalUncertaintyForWord);
                bestWord = dictionaryWord;
                bestUncertainty = totalUncertaintyForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                //System.out.println("PercentDone: " + percentDone);
            }

        }

        System.out.println("Best word: " + bestWord + " solutions: " + bestUncertainty);
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
