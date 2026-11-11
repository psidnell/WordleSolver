package com.psidnell.words;

import com.psidnell.ruleset.RuleSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class WordList {

    protected final Set<Word> words = new TreeSet<>();

    public WordList() {
    }

    public WordList(WordList o) {
        words.addAll(o.words);
    }

    public WordList(Collection<Word> words) {
        this.words.addAll(words);
    }

    public Collection<Word> getWords() {
        return words;
    }

    public void prune(RuleSet match) {
        Collection<Word> wordsRemaining = new HashSet<>(words);
        wordsRemaining.forEach(word -> {
            if (!match.match(word)) {
                words.remove(word);
            }
        });
    }

    public boolean contains(Word word) {
        return words.contains(word);
    }

    public int size() {
        return words.size();
    }

    public boolean hasWord(Word word) {
        return words.contains(word);
    }

    public void add(Word word) {
        this.words.add(word);
    }

    public void remove(Word guess) {
        words.remove(guess);
    }

    public Word getOnlyWord() {
        if (words.size() != 1) {
            throw new IllegalStateException("There are " + words.size() +" words");
        }
        return words.stream().findFirst().orElseThrow();
    }
}
