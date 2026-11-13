package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.Wordle;
import com.psidnell.words.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdaptiveSolutionEvaluator implements SolutionEvaluator {

    @Autowired
    private Wordle wordle;

    @Value("${SearchThreshold1}")
    private int searchThreshold1;

    @Value("${SearchThreshold2}")
    private int searchThreshold2;


    @Override
    public Word evaluateBestWord() {
        GameState state = wordle.getState();

        if (state.getGuessesRemaining() == 6) {
           return new EarlyGuessSolutionEvaluator(state).evaluateBestWord();
        }

        int solutionCount = state.getSolutions().getWords().size();

        // Greater than: Uses solution 1
        final int l1 = searchThreshold1;
        // Between: Uses solution 2
        final int l2 = searchThreshold2;
        // Between: Uses solution 3

        if (solutionCount > l1) { // Max to L1
            System.out.println("(1) Quick Search on reduced guesses above " + l1);
            GameState subState = new GameState(state, state.getSolutions());
            return new RankSolutionEvaluator(subState).evaluateBestWord();
        } else if (solutionCount > l2) { // L1..L2
            System.out.println("(2) Exhaustive Search on reduced guesses between " + l2 + " and " + l1);
            GameState subState = new GameState(state, state.getSolutions());
            return new RuleSetSolutionEvaluator(subState).evaluateBestWord();
        } else { // Below L2
            System.out.println("(3) Exhaustive Search on maximum guesses below " + l2);
            GameState subState = new GameState(state);
            return new RuleSetSolutionEvaluator(subState).evaluateBestWord();
        }
    }
}
