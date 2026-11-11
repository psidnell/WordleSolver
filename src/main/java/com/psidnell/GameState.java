package com.psidnell;

import com.psidnell.ruleset.RuleSet;
import com.psidnell.words.Word;
import com.psidnell.words.WordList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GameState {
    private final WordList dictionary;
    private final WordList solutions;
    private final List<Word> previousOrderedGuesses;
    private final WordList allGuesses;
    private RuleSet combinedRuleSet;
    private final List<String> history = new LinkedList<>();

    public int guessesMade;
    private int guessesRemaining;

    public GameState(WordList dictionary, WordList solutions) {
        this.dictionary = dictionary;
        this.solutions = solutions;
        combinedRuleSet = new RuleSet();
        allGuesses = new WordList();
        previousOrderedGuesses = new ArrayList<>(6);
        guessesMade = 0;
        guessesRemaining = 6;
    }

    public GameState(GameState o) {
        this.dictionary = new WordList(o.dictionary);
        this.solutions = new WordList(o.solutions);
        combinedRuleSet = new RuleSet().combineRuleSet(o.combinedRuleSet);
        allGuesses = new WordList(o.allGuesses);
        previousOrderedGuesses = new ArrayList<>(o.previousOrderedGuesses);
        guessesMade = o.guessesMade;
        guessesRemaining = o.guessesRemaining;
    }

    public GameState(GameState o, WordList dictionary) {
        this.dictionary = new WordList(dictionary);
        this.solutions = new WordList(o.solutions);
        combinedRuleSet = new RuleSet().combineRuleSet(o.combinedRuleSet);
        allGuesses = new WordList(o.allGuesses);
        previousOrderedGuesses = new ArrayList<>(o.previousOrderedGuesses);
        guessesMade = o.guessesMade;
        guessesRemaining = o.guessesRemaining;
    }

    public void registerGuess(Word guess, RuleSet ruleSetForGuess) {
        if (guessesRemaining == 0) {
            throw new PlayerException("No guesses remaining");
        }
        if (allGuesses.contains(guess)) {
            throw new PlayerException("Have already guessed: " + guess);
        }
        if (!dictionary.contains(guess)) {
            throw new PlayerException("This word is not in the dictionary: " + guess);
        }

        int solutionsBefore = solutions.size();
        combinedRuleSet = combinedRuleSet.combineRuleSet(ruleSetForGuess);
        solutions.prune(combinedRuleSet);
        previousOrderedGuesses.add(guess);
        allGuesses.add(guess);
        dictionary.remove(guess);
        guessesRemaining--;
        guessesMade++;

        StringBuilder b = new StringBuilder();
        b.append(guessesMade).append(' ').append(guess).append(' ').append(combinedRuleSet).append(" Solutions ")
                .append(solutionsBefore).append(" -> ").append(solutions.size());
        history.add(b.toString());

        if (solutions.size() == 0 && !combinedRuleSet.isSolved()) {
            throw new IllegalStateException("Solutions now empty but game is not solved");
        }
    }

    public WordList getDictionary() {
        return dictionary;
    }

    public WordList getSolutions() {
        return solutions;
    }

    public List<Word> getPreviousGuesses() {
        return Collections.unmodifiableList(previousOrderedGuesses);
    }

    public int getGuessesMade() {
        return guessesMade;
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    public Word getLastGuess() {
        return previousOrderedGuesses.getLast();
    }

    public boolean haveAlreadyGuessed(Word guess) {
        return previousOrderedGuesses.contains(guess);
    }

    public RuleSet getCombinedRuleSet() {
        return combinedRuleSet;
    }

    public boolean isSolved() {
        return combinedRuleSet.isSolved();
    }

    public List<String> getHistory() {
        return history;
    }
}
