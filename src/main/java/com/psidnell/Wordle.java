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

        if (guess.length() != 5) {
            System.out.println("Guess must be 5 characters");
            return true;
        }

        //TODO for testing
//        if (!dictionary.isWord(guess)) {
//            System.out.println("Unknown word");
//            return false;
//        }

        Word guessWord = new Word(guess);
        Match match = new Match(guessWord, solution);
        System.out.println(match);
        if (guessWord.equals(solution)) {
            System.out.println("SUCCESS Guessed correctly!");
            return false;
        }

        guessesRemaining--;

        if (guessesRemaining == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        return true;
    }
}
