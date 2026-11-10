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

public class RecursiveEvaluator implements SolutionEvaluator {
    private static final System.Logger LOG = System.getLogger(RecursiveEvaluator.class.getName());

    private final GameState state;

    public RecursiveEvaluator(GameState state) {
        this.state = state;
    }

    @Override
    public Word evaluateBestWord() {
        TreeMap<Word, Outcome> allOutcomes = new TreeMap<>();
        int totalCombinations = state.getDictionary().size() * state.getSolutions().size();
        LOG.log(Level.INFO, "Total solution/guess combinations to try: {0}", totalCombinations);

        for (Word solution : state.getSolutions().getWords()) {
            LOG.log(Level.INFO, "Dry run {0}", solution);
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
//                LOG.log(Level.INFO, "Best word: so far {0} Failures: {1}", bestWord.get(), lowestFailures.get());
//            }
        }

        Outcome bestOutcome = allOutcomes.values().stream().findFirst().orElseThrow();

        LOG.log(Level.INFO, "Best outcome:{0}", bestOutcome);

        return bestOutcome.guess;
    }

    /**
     * Return the Guess with the lowest failure count
     * @param state
     * @param solution
     * @return
     */
    private Outcome dryRun(GameState state, Word solution) {
        Map<Word, Outcome> allOutcomes = new HashMap<>();

        for (Word guess : state.getDictionary().getWords()) {
            GameState subState = new GameState(state, state.getDictionary());
            RuleSet r = new RuleSet(guess, solution);
            subState.registerGuess(guess, r);
            Outcome outcome = new Outcome(guess, solution);
            solve(subState, solution, outcome);

            allOutcomes.put(outcome.guess, outcome);

            //LOG.log(Level.INFO, "Word: {0}", guess);
        }

        // Find the worst case
        // TODO prefer solution

        TreeSet<Outcome> sortedOutcomes = new TreeSet<>(allOutcomes.values());
        Outcome bestGuess = sortedOutcomes.first();

        LOG.log(Level.INFO, "Best dry run for {0}", bestGuess);

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
    private void solve(GameState state, Word solution, Outcome outcome) {
        outcome.solutionCount += state.getSolutions().size();

        if (state.isSolved()) {
            LOG.log(Level.INFO, "Solved {0}", state.getPreviousGuesses());
            outcome.fastestGuess = Math.min(outcome.fastestGuess, state.getGuessesMade());
            outcome.slowestGuess = Math.max(outcome.slowestGuess, state.getGuessesMade());
            outcome.successes++;
            return;
        }

        if (state.getGuessesRemaining() == 0) {
            LOG.log(Level.INFO, "Failed {0}", state.getPreviousGuesses());
            outcome.failures++;
            return;
        }

        for (Word guess : state.getSolutions().getWords()) {
            GameState subState = new GameState(state, state.getDictionary());
            RuleSet r = new RuleSet(guess, solution);
            subState.registerGuess(guess, r);
            solve(subState, solution, outcome);
        }
    }

    private static class Outcome implements Comparable<Outcome>{
        final Word guess;
        final Word solution;
        int fastestGuess = Integer.MAX_VALUE;
        int slowestGuess = 0;
        int successes = 0;
        int failures = 0;
        int solutionCount = 0;

        Outcome(Word word, Word solution) {
            this.guess = word;
            this.solution = solution;
        }

        void merge(Outcome o) {
            fastestGuess = Math.min(fastestGuess, o.fastestGuess);
            slowestGuess = Math.max(slowestGuess, o.slowestGuess);
            solutionCount += o.solutionCount;
            successes += o.successes;
            failures += o.failures;
        }

        @Override
        public int compareTo(Outcome o) {
            int result = 0;

            // Lowest
            result = Integer.compare(solutionCount, o.solutionCount);
            if (result != 0) {
                return result;
            }

            // Lowest
            result = Integer.compare(failures, o.failures);
            if (result != 0) {
                return result;
            }

            // Highest
            result = Integer.compare(o.failures, failures);
            if (result != 0) {
                return result;
            }

            // Highest
            result = Integer.compare(o.slowestGuess, slowestGuess);
            if (result != 0) {
                return result;
            }

            // Lowest
            result = Integer.compare(o.fastestGuess, fastestGuess);
            if (result != 0) {
                return result;
            }

            result = guess.compareTo(o.guess);
            if (result != 0) {
                return result;
            }

            result = solution.compareTo(o.solution);
            if (result != 0) {
                return result;
            }

            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Outcome outcomes)) return false;
            return fastestGuess == outcomes.fastestGuess &&
                    slowestGuess == outcomes.slowestGuess &&
                    successes == outcomes.successes &&
                    failures == outcomes.failures &&
                    solutionCount == outcomes.solutionCount &&
                    Objects.equals(solution, outcomes.solution) &&
                    Objects.equals(guess, outcomes.guess);
        }

        @Override
        public int hashCode() {
            return Objects.hash(solution, guess, fastestGuess, slowestGuess, successes, failures, solutionCount);
        }

        @Override
        public String toString() {
            return "Outcome{" +
                    "guess=" + guess +
                    ", solution=" + solution +
                    ", fastestGuess=" + fastestGuess +
                    ", slowestGuess=" + slowestGuess +
                    ", successes=" + successes +
                    ", failures=" + failures +
                    ", solutionCount=" + solutionCount +
                    '}';
        }
    }
}
