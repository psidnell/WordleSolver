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
    private final List<Word> previousOrderedGuesses = new ArrayList<>(6);
    private final WordList allGuesses = new WordList();
    private RuleSet combinedRuleSet = new RuleSet();

    public int guessesMade = 0;
    private int guessesRemaining = 6;

    public GameState(Dictionary dictionary, WordList solutions) {
        this.dictionary = dictionary;
        this.solutions = solutions;
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

        combinedRuleSet = combinedRuleSet.combineRuleSet(ruleSetForGuess);
        solutions.prune(combinedRuleSet);
        previousOrderedGuesses.add(guess);
        allGuesses.add(guess);
        dictionary.remove(guess);
        guessesRemaining--;
        guessesMade++;

        if (solutions.size() == 0) {
            throw new IllegalStateException("Solutions now empty");
        }
    }

    public Dictionary getDictionary() {
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
}
