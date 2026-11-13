package com.psidnell.solution;

import com.psidnell.GameState;
import com.psidnell.ruleset.RuleSet;
import com.psidnell.words.Word;

import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This (or something like it) looks like it could work, but the time/memory requirements are exponential.
 */
public class RecursiveSolutionEvaluator implements SolutionEvaluator {
    private static final System.Logger LOG = System.getLogger(RecursiveSolutionEvaluator.class.getName());

    private final GameState state;

    public RecursiveSolutionEvaluator(GameState state) {
        this.state = state;
    }


    // For every possible guess, and all guesses below it ensure that the game is soluble

    @Override
    public Word evaluateBestWord() {
        TreeMap<Word, Outcome> allOutcomes = new TreeMap<>();
        int totalCombinations = state.getDictionary().size() * state.getSolutions().size();
        LOG.log(Level.INFO, "Total solution/guess combinations to try: {}", totalCombinations);

        for (Word solution : state.getSolutions().getWords()) {
            LOG.log(Level.INFO, "Dry run {}", solution);
            GameState subState = new GameState(state);
            Outcome outcome = dryRun(subState, solution);

            if (allOutcomes.containsKey(outcome.guess)) {
                allOutcomes.get(outcome.guess).merge(outcome);
            } else {
                allOutcomes.put(outcome.guess, outcome);
            }

//            // TODO prefer solution
//            if (p.right() < lowestFailures.get()) {
//                lowestFailures.set(p.right());
//                bestWord.set(p.left());
//                LOG.log(Level.INFO, "Best word: so far {} Failures: {}", bestWord.get(), lowestFailures.get());
//            }
        }

        Outcome bestOutcome = allOutcomes.values().stream().findFirst().orElseThrow();

        LOG.log(Level.INFO, "Best outcome:{}", bestOutcome);

        return bestOutcome.guess;
    }

    /**
     * Return the Guess with the lowest failure count
     * @param state
     * @param solution
     * @return
     */
    private static Outcome dryRun(GameState state, Word solution) {
        Map<Word, Outcome> allOutcomes = new HashMap<>();

        for (Word guess : state.getDictionary().getWords()) {
            GameState subState = new GameState(state, state.getDictionary());
            RuleSet r = new RuleSet(guess, solution);
            subState.registerGuess(guess, r);

            Outcome outcome = findBestOutcome(subState, solution);
            if (outcome != null) {
                allOutcomes.put(outcome.guess, outcome);
            }

            //LOG.log(Level.INFO, "Word: {}", guess);
        }

        // Find the worst case
        // TODO prefer solution from dictionary

        TreeSet<Outcome> sortedOutcomes = new TreeSet<>(allOutcomes.values());
        Outcome bestGuess = sortedOutcomes.first();

        LOG.log(Level.INFO, "Best dry run for {}", bestGuess);

        return bestGuess;
    }

    /**
     * We have hypothesised the solution and have made a guess.
     * Play all possible sub games.
     *
     * @param state
     * @param solution
     * @return
     */
    private static Outcome findBestOutcome(GameState state, Word solution) {

        if (state.getGuessesRemaining() == 0) {
            //LOG.log(Level.INFO, "Failed if {} when {}", solution, state.getPreviousGuesses());
            return null;
        }

        if (state.getLastGuess().equals(solution)) {
            LOG.log(Level.INFO, "Solved if {} then {}", solution, state.getPreviousGuesses());
            return new Outcome(solution, 1);
        }

        TreeSet<Outcome> outcomes = new TreeSet<>();
        for (Word guess : state.getDictionary().getWords()) {
            GameState subState = new GameState(state);
            RuleSet r = new RuleSet(guess, solution);
            subState.registerGuess(guess, r);
            Outcome outcome = findBestOutcome(subState, solution);
            if (outcome !=  null) {
                outcomes.add(outcome);
            }
        }
        return outcomes.isEmpty() ? null : outcomes.first();
    }

    private static class Outcome implements Comparable<Outcome> {
        final Word guess;
        int solutionCount = 0;

        Outcome(Word word, int solutionCount) {
            this.guess = word;
            this.solutionCount = solutionCount;
        }

        void merge(Outcome o) {
            solutionCount += o.solutionCount;
        }

        @Override
        public int compareTo(Outcome o) {
            return Integer.compare(solutionCount, o.solutionCount);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Outcome outcomes)) return false;
            return Objects.equals(guess, outcomes.guess) &&
                    solutionCount == outcomes.solutionCount;

        }

        @Override
        public int hashCode() {
            return Objects.hash(guess, solutionCount);
        }

        @Override
        public String toString() {
            return "Outcome{" +
                    "guess=" + guess +
                    ", solutionCount=" + solutionCount +
                    '}';
        }
    }
}
