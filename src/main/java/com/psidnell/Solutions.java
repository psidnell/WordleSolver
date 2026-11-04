package com.psidnell;

import com.psidnell.rules.RuleSet;
import com.psidnell.words.Dictionary;
import com.psidnell.words.Word;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Solutions {

    private final Set<Word> solutions;

    public Solutions(Dictionary dictionary) {
        solutions = new TreeSet<>(dictionary.getWords());
    }

    public Collection<Word> getSolutions() {
        return solutions;
    }

    public void prune(RuleSet match) {
        Collection<Word> wordsRemaining = new HashSet<>(solutions);
        wordsRemaining.forEach(word -> {
            if (!match.match(word)) {
                solutions.remove(word);
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
