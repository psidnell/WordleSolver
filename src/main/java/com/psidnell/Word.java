package com.psidnell;

import java.util.HashMap;
import java.util.Map;

public class Word {
    private final String word;
    private final Map<Character, Integer> occurrences = new HashMap<>();

    public Word(String word) {
        this.word = word;
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            Integer count = occurrences.get(c);
            if (count != null) {
                occurrences.put(c, count + 1);
            } else {
                occurrences.put(c, 1);
            }
        }
    }

    public String getWord() {
        return word;
    }

    public int occurencesOf(Character c) {
        Integer occurences =  occurrences.get(c);
        if (occurences == null) {
            return 0;
        }
        return occurences;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            return ((Word) obj).word.equals(word);
        }
        return false;
    }

    @Override
    public String toString() {
        return word;
    }
}
