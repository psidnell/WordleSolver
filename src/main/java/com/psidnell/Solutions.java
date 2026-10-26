package com.psidnell;

import java.util.*;

public class Solutions {

    private Map<String, Word> solutions;

    public Solutions(Dictionary dictionary) {
        solutions = new TreeMap<>(dictionary.getWords());
    }

    public Solutions(Solutions solutions) {
        this.solutions = new TreeMap<>(solutions.solutions);
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
