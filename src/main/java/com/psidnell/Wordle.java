package com.psidnell;

public class Wordle extends ComponentImpl {

    private Word solution;
    int guessesRemaining = 6;

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

    public boolean guess(String guess) {

        if (solution == null) {
            System.out.println("No solution is set");
            return true;
        }

        if (guess.length() != 5) {
            System.out.println("Guess must be 5 characters");
            return true;
        }

        if (!dictionary.isWord(guess)) {
            System.out.println("Unknown word");
            return false;
        }

        // We have a valid guess
        guessesRemaining--;

        Word guessWord = new Word(guess);
        Match match = new Match(guessWord, solution);
        System.out.println(match);
        if (guessWord.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses" );
            return false;
        }



        if (guessesRemaining == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        return true;
    }
}
