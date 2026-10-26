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

    public void guess(String guess) {

        if (guessesRemaining == 0) {
            System.out.println("No more guesses!");
            return;
        }

        if (guess.length() != 5) {
            System.out.println("Guess must be 5 characters");
            return;
        }

        //TODO for testing
//        if (!dictionary.isWord(guess)) {
//            System.out.println("Unknown word");
//            return;
//        }

        Word guessWord = new Word(guess);
        Match match = new Match(guessWord, solution);
        System.out.println(match);
        if (guessWord.equals(solution)) {
            System.out.println("Guessed correctly!");
        }

        guessesRemaining--;
    }
}
