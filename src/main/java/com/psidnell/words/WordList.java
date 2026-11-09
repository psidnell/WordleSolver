package com.psidnell.words;

import com.psidnell.ruleset.RuleSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

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

    //todo no?
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

    public int countWordsNotMatching(RuleSet match) {
        AtomicInteger count = new AtomicInteger(0);
        words.forEach(word -> {
            if (!match.match(word)) {
                count.incrementAndGet();
            }
        });
        return count.get();
    }

    public boolean contains(Word word) {
        return words.contains(word);
    }

    public int size() {
        return words.size();
    }

    public boolean hasWord(String word) {
        return words.contains(new Word(word));
    }

    public void addAll(WordList other) {
        this.words.addAll(other.getWords());
    }

    public void add(Word word) {
        this.words.add(word);
    }

    public void remove(Word guess) {
        words.remove(guess);
    }
}
