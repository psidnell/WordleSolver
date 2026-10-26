package com.psidnell;

public class Wordle extends ComponentImpl {

    private Word solution;
    int guessesRemaining = 6;
    private Solutions solutions;

    @Override
    public void init() {
        super.init();
        this.solutions = new Solutions(dictionary);
    }

    public void setSolution(String solution) {
        if (!dictionary.isWord(solution)) {
            throw new IllegalArgumentException("solution is not a valid word");
        }
        this.solution = new Word (solution);
    }

    public void printStatus() {
        System.out.println("Solutions: " + solutions.getSolutions().size());
        System.out.println("Guesses Remaining: " + guessesRemaining);
    }

    // TODO Move all of the tests snd printing here
    public boolean userGuess(String guess) {
        return guess(new Word(guess), solution, solutions, true);
    }

    public boolean guess(Word guess, Word solution, Solutions solutions, boolean print) {

//        if (solution == null) {
//            System.out.println("No solution is set");
//            return true;
//        }

        if (guess.getWord().length() != 5) {
            System.out.println("Guess must be 5 characters");
            return true;
        }

        if (!dictionary.isWord(guess.getWord())) {
            System.out.println("Unknown word");
            return false;
        }

        // We have a valid guess
        guessesRemaining--;

        Match match = new Match(guess, solution);
        if (print) {
            System.out.println(match);
        }

        solutions.prune(match);

        if (print && guess.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses" );
            return false;
        }
//
//        if (guessesRemaining == 0) {
//            System.out.println("FAILED! No more guesses!");
//            return false;
//        }

        return true;
    }

    public void showRemaining() {
        for (Word word : solutions.getSolutions()) {
            System.out.println(word);
        }
    }

    public Word propose() {
        // Create a new wordle instance and guess with every dictionary word
        // Remember the one with the smallest resultant solutions number
        int minSolutionSize = Integer.MAX_VALUE;
        Word bestWord = null;

        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        for (Word dictionaryGuess: dictionary.getWords().values()) {

            System.out.println("Trying: " + dictionaryGuess);

            int totalSolutionSizeForGuess = 0;

            for (Word posSolution : solutions.getSolutions()) {
                Solutions nextSolutions = new Solutions(solutions);
                guess(dictionaryGuess, posSolution, nextSolutions, false);
                totalSolutionSizeForGuess += nextSolutions.getSolutions().size();
                if (totalSolutionSizeForGuess > minSolutionSize) {
                    System.out.println("Abandoning: " + dictionaryGuess + " solutions: " + totalSolutionSizeForGuess);
                    break;
                }
            }

            if (totalSolutionSizeForGuess < minSolutionSize) {
                System.out.println("Best word: " + dictionaryGuess + " solutions: " + totalSolutionSizeForGuess +
                        " dropped: " + (minSolutionSize - totalSolutionSizeForGuess));
                minSolutionSize = totalSolutionSizeForGuess;
                bestWord = dictionaryGuess;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                System.out.println("PercentDone: " + percentDone);
            }

        }
        return bestWord;
    }
}
