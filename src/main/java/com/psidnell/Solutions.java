package com.psidnell;

import com.psidnell.rules.RuleSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solutions {

    private Set<Word> solutions;

    public Solutions(Dictionary dictionary) {
        solutions = new TreeSet<>(dictionary.getWords());
    }

    public Solutions(Solutions solutions) {
        this.solutions = new TreeSet<>(solutions.solutions);
    }

    public Collection<Word> getSolutions() {
        return solutions;
    }

    public void prune(RuleSet match) {
        Collection<Word> wordsRemaining = new HashSet<>(solutions);
        AtomicInteger count = new AtomicInteger(0);
        wordsRemaining.forEach(word -> {
            if (!match.match(word)) {
                solutions.remove(word);
                count.incrementAndGet();
            }
        });
    }

    public int countWordsNotMatching(RuleSet match) {
        AtomicInteger count = new AtomicInteger(0);
        solutions.forEach(word -> {
            if (!match.match(word)) {
                count.incrementAndGet();
            }
        });
        return count.get();
    }
}
