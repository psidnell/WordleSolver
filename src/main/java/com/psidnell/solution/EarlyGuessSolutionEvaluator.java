package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.words.Word;

public class EarlyGuessSolutionEvaluator implements SolutionEvaluator {
    public static String FIRST_GUESS = "trace";
    private final GameState state;

    public EarlyGuessSolutionEvaluator(GameState state) {
        this.state = state;
    }

    @Override
    public Word evaluateBestWord() {
        if (state.getGuessesMade() != 0) {
            throw new IllegalStateException("Nor first guess");
        }
        System.out.println("First Guess:" + EarlyGuessSolutionEvaluator.FIRST_GUESS);
        return new Word(FIRST_GUESS);
    }
}
