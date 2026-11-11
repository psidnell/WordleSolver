package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.util.ComponentImpl;
import com.psidnell.words.Word;

public class Strategy extends ComponentImpl {
    public static String FIRST_GUESS = "trace";

    public SolutionEvaluator getOptimalStrategy(GameState state) {

        if (state.getGuessesRemaining() == 6) {
            System.out.println("First Guess:" + FIRST_GUESS);
            return () -> new Word(FIRST_GUESS);
        }

        int solutionCount = state.getSolutions().getWords().size();

        // Greater than: Uses solution 1
        final int l1 = settings.getSearchThreshold1();
        // Between: Uses solution 2
        final int l2 = settings.getSearchThreshold2();
        // Between: Uses solution 3

        if (solutionCount > l1) { // Max to L1
            System.out.println("(1) Quick Search on reduced guesses above " + l1);
            GameState subState = new GameState(state, state.getSolutions());
            return new RankSolutionEvaluator(subState);
        } else if (solutionCount > l2) { // L1..L2
            System.out.println("(2) Exhaustive Search on reduced guesses between " + l2 + " and " + l1);
            GameState subState = new GameState(state, state.getSolutions());
            return new RuleSetSolutionEvaluator(subState);
        } else { // Below L2
            System.out.println("(3) Exhaustive Search on maximum guesses below " + l2);
            GameState subState = new GameState(state);
            return new RuleSetSolutionEvaluator(subState);
        }
//        else {
//            System.out.println("(4) Recursive Search on maximum guesses below " + l2);
//            GameState subState = new GameState(state);
//            return new RecursiveSolutionEvaluator(subState);
//        }
    }
}
