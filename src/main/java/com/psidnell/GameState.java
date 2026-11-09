package com.psidnell;

import com.psidnell.ruleset.RuleSet;
import com.psidnell.words.Dictionary;
import com.psidnell.words.Word;
import com.psidnell.words.WordList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {
    private final Dictionary dictionary;
    private final WordList solutions;
    private final List<Word> previousGuesses = new ArrayList<>(6);
    private RuleSet combinedRuleSet = new RuleSet();

    public int guessesMade = 0;
    private int guessesRemaining = 6;

    public GameState(Dictionary dictionary, WordList solutions) {
        this.dictionary = dictionary;
        this.solutions = solutions;
    }

    public void registerGuess(Word guess, RuleSet ruleSetForGuess) {
        combinedRuleSet = combinedRuleSet.combineRuleSet(ruleSetForGuess);
        solutions.prune(combinedRuleSet);
        previousGuesses.add(guess);
        guessesRemaining--;
        guessesMade++;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public WordList getSolutions() {
        return solutions;
    }

    public List<Word> getPreviousGuesses() {
        return Collections.unmodifiableList(previousGuesses);
    }

    public int getGuessesMade() {
        return guessesMade;
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    public Word getLastGuess() {
        return previousGuesses.getLast();
    }

    public boolean haveAlreadyGuessed(Word guess) {
        return previousGuesses.contains(guess);
    }

    public RuleSet getCombinedRuleSet() {
        return combinedRuleSet;
    }
}
