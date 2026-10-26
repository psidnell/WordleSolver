package com.psidnell;

import java.util.*;

public class Solutions extends ComponentImpl {

    private Map<String, Word> solutions;

    @Override
    public void init() {
        super.init();
        this.solutions = new TreeMap<>(dictionary.getWords());
    }

    public Collection<Word> getSolutions() {
        return (Collection<Word>) solutions.values();
    }

    public void prune(Match match) {
        Collection<Word> wordsRemaining = new HashSet<>(solutions.values());
        wordsRemaining.forEach(word -> {
            if (match.wouldEliminateWord(word)) {
                solutions.remove(word.getWord());
            }
        });
    }
}
