package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.ruleset.RuleSet;
import com.psidnell.util.Pair;
import com.psidnell.words.Word;
import com.psidnell.words.WordList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RecursiveEvaluator implements SolutionEvaluator {
    private static final System.Logger LOG = System.getLogger(RecursiveEvaluator.class.getName());

    private final GameState state;
    private final WordList guesses;

    public RecursiveEvaluator(GameState state, WordList guesses) {
        this.state = state;
        this.guesses = guesses;
    }

    @Override
    public Word evaluateBestWord() {
        int totalCombinations = guesses.size() * state.getSolutions().size();
        LOG.log(System.Logger.Level.INFO, "Total solution/guess combinations to try: {0}", totalCombinations);

        AtomicInteger lowestFailures = new AtomicInteger(Integer.MAX_VALUE);
        AtomicReference<Word> bestWord = new AtomicReference<>(null);

        for (Word solution : state.getSolutions().getWords()) {
            LOG.log(System.Logger.Level.INFO, "Dry run {0}", solution);
            GameState subState = new GameState(state, guesses);
            Pair<Word, Integer> p = dryRun(subState, solution);

            // TODO prefer solution
            if (p.right() < lowestFailures.get()) {
                lowestFailures.set(p.right());
                bestWord.set(p.left());
                LOG.log(System.Logger.Level.INFO, "Best word: so far {0} Failures: {1}", bestWord.get(), lowestFailures.get());
            }
        }

        LOG.log(System.Logger.Level.INFO, "Best word: {0} Failures: {1}", bestWord.get(), lowestFailures.get());

        return bestWord.get();
    }

    /**
     * Return the Guess with the lowest failure count
     * @param state
     * @param solution
     * @return
     */
    private Pair<Word, Integer> dryRun(GameState state, Word solution) {
        Map<Word, Integer> counts = new HashMap<>();

        for (Word guess : state.getSolutions().getWords()) {
            GameState subState = new GameState(state, state.getDictionary());
            RuleSet r = new RuleSet(guess, solution);
            subState.registerGuess(guess, r);
            int failuresForGuess = recurse(subState, solution);
            counts.put(guess, failuresForGuess);
        }

        // Find the worst case
        // TODO prefer solution
        AtomicReference<Pair<Word, Integer>> bestWord = new AtomicReference<>(null);
        AtomicInteger lowestFailures = new AtomicInteger(Integer.MAX_VALUE);
        counts.forEach((w, c) -> {
            if (c < lowestFailures.get()) {
                bestWord.set(new Pair<>(w, c));
                lowestFailures.set(c);
            }
        });

        LOG.log(System.Logger.Level.INFO, "Best dry run for {0} is {1} with {2}", solution, bestWord, lowestFailures);

        return bestWord.get();
    }

    /**
     * Return the failure count
     * @param state
     * @param solution
     * @return
     */
    private int recurse(GameState state, Word solution) {
        if (state.isSolved()) {
            //LOG.log(Level.INFO, "Solved {0}", state.getPreviousGuesses());
            return 0;
        }

        if (state.getGuessesRemaining() == 0) {
            //LOG.log(Level.INFO, "Failed {0}", state.getPreviousGuesses());
            return 1;
        }

        int count = 0;
        for (Word guess : state.getSolutions().getWords()) {
            GameState subState = new GameState(state, state.getDictionary());
            RuleSet r = new RuleSet(guess, solution);
            subState.registerGuess(guess, r);
            count += recurse(subState, solution);
        }
        return count;
    }
}
