package com.psidnell;

public class Wordle extends ComponentImpl {

    private Word solution;

    public void setSolution(String solution) {
        if (!dictionary.isWord(solution)) {
            throw new IllegalArgumentException("solution is not a valid word");
        }
        this.solution = new Word (solution);
    }

    public void printStatus() {
        System.out.println("Solutions: " + solutions.getSolutions().size());
    }

    public void guess(String guess) {
        if (guess.length() != 5) {
            System.out.println("Guess must be 5 characters");
            return;
        }

        //TODO for testing
//        if (!dictionary.isWord(guess)) {
//            System.out.println("Unknown word");
//            return;
//        }

        Match match = new Match(new Word(guess), solution);
        System.out.println(match);
    }
}
