package com.psidnell;

import com.psidnell.rules.RuleSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public int prune(RuleSet match) {
        Collection<Word> wordsRemaining = new HashSet<>(solutions.values());
        AtomicInteger count = new AtomicInteger(0);
        wordsRemaining.forEach(word -> {
            if (match.wouldEliminateWord(word)) {
                solutions.remove(word.getWord());
                count.incrementAndGet();
            }
        });
        return count.get();
    }
}
