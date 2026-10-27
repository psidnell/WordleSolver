package com.psidnell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Wordle extends ComponentImpl {

    private Word solution;
    int guessesRemaining = 6;
    private Solutions solutions;
    private final List<Integer> userGuessSolutionsRemaining = new ArrayList<>();
    private final List<Match> userMatches = new ArrayList<>();


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
        System.out.println("History:");
        for(int i=0; i < userMatches.size(); i++) {
            System.out.println("  - " + " " +  userMatches.get(i) + " " + userGuessSolutionsRemaining.get(i));
        }
        System.out.println("Solutions: " + solutions.getSolutions().size());
        System.out.println("Guesses Remaining: " + guessesRemaining);
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
        guessesRemaining--;

        Match match = guess(guessWord, solution, solutions);
        userGuessSolutionsRemaining.add(solutions.getSolutions().size());
        userMatches.add(match);

        System.out.println(match);


        if (match.isMatch()) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses" );
            return true;
        }

        return false;
    }

    public Match guess(Word guess, Word solution, Solutions solutions) {
        Match match = new Match(guess, solution);
        int pruned = solutions.prune(match);
        return match;
    }

    public void showRemaining() {
        for (Word word : solutions.getSolutions()) {
            System.out.println(word);
        }
    }

    public String propose() {
        // Create a new wordle instance and guess with every dictionary word
        // Remember the one with the smallest resultant solutions number

        // Shouldn't be necessary
        if (solutions.getSolutions().size() == 1) {
            return solutions.getSolutions().stream().findFirst().get().getWord();
        }

        int minSolutionSize = Integer.MAX_VALUE;
        Word bestWord = null;

        //int startSolutionSize = solutions.getSolutions().size();
        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        int maxWordsEliminated = 0;

        int worstCaseMatches = dictWords * solutions.getSolutions().size();
        int bestSolutionSize = worstCaseMatches;

        int totalSolutionsForWord = 0;

        for (Word dictionaryGuess: dictionary.getWords().values()) {
        //for (Word dictionaryGuess: solutions.getSolutions()) { //todo only consider guesses from remaining solutions?

            if (conflictsWithPreviousMatches(dictionaryGuess)) {
                //System.out.println("Conflict with prev guesses: " + dictionaryGuess);
                continue;
            }

            //System.out.println("Best: " + bestWord + " Trying: " + dictionaryGuess);

            int totalSolutionSizeForGuess = 0;
            int totalWordsEliminated = 0;
//            int totalImprovementsForGuess = 0 ;
//            int maxImprovementsForGuess = 0;

            for (Word posSolution : solutions.getSolutions()) {
                // Can I bail early if words don't overlap?
                // Put this condition around the rest?
                //todo not hitting this Why?
//                if (!dictionaryGuess.haveCommonCharacters(posSolution)) {
//                    // If the words have nothing in common then we would learn nothing from guess
//                    System.out.println("No Overlap: " + dictionaryGuess + "/" + posSolution);
//                    //totalSolutionSizeForGuess += startSolutionSize;
//                    continue;
//                }


                // TODO Get the core working before optimise
                Solutions nextSolutions = new Solutions(solutions);
                int initialSolutionSize = solutions.getSolutions().size();
                Match m = guess(dictionaryGuess, posSolution, nextSolutions);
                int guessSolutionsSize = nextSolutions.getSolutions().size();
                totalSolutionsForWord += guessSolutionsSize;

                int wordsEliminated = initialSolutionSize - guessSolutionsSize;
                totalWordsEliminated += wordsEliminated;

                int percent = (100 * wordsEliminated) / initialSolutionSize;
                //System.out.println("Eliminated: " + dictionaryGuess + "/" + posSolution + " " + wordsEliminated + " " + percent + "%");

//                totalSolutionSizeForGuess += guessSolutionsSize;
                if (totalSolutionsForWord >= bestSolutionSize) {
                    // If we're never going to get under the minimum permutations
                    // for this guess then abort
                    //System.out.println("Too many solutions: " + dictionaryGuess + " solutions: " + totalSolutionsForWord);
                    break;
                }


                //System.out.println(totalSolutionSizeForGuess);
            }

            //System.out.println("Analysed: " + dictionaryGuess + " solutions: " + totalSolutionSizeForGuess);

//            if (totalSolutionSizeForGuess < minSolutionSize) {
//                System.out.println("Best word: " + dictionaryGuess + " solutions: " + totalSolutionSizeForGuess +
//                        " dropped: " + (minSolutionSize - totalSolutionSizeForGuess));
//                minSolutionSize = totalSolutionSizeForGuess;
//                bestWord = dictionaryGuess;
//            }
            if (totalSolutionsForWord < bestSolutionSize) {
                int percentage = (totalWordsEliminated * 100) / worstCaseMatches;
                System.out.println("Best word so far: " + dictionaryGuess + " eliminated: " + totalSolutionsForWord);
                bestWord = dictionaryGuess;
                bestSolutionSize = totalSolutionsForWord;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                System.out.println("PercentDone: " + percentDone);
            }

        }

        System.out.println("Best word: " + bestWord + " solutions: " + minSolutionSize);
        return bestWord.getWord();
    }

    private boolean conflictsWithPreviousMatches(Word guess) {
        for (Match m : userMatches) {
            if (m.getGuess().equals(guess) || m.wouldEliminateWord(guess)) {
                return true;
            }
        }
        return false;
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }
}
