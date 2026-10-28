package com.psidnell.rules;

import java.util.Map;
import java.util.TreeMap;

public class CharacterFrequency {
    private final Map<Character, Integer> frequencies;
    
    public CharacterFrequency() {
        frequencies = new TreeMap<>();
    }

    public CharacterFrequency(CharacterFrequency other) {
        frequencies = new TreeMap<>(other.frequencies);
    }
    
    public void increase(Character c) {
        int count = frequencies.getOrDefault(c, 0);
        frequencies.put(c, count + 1);
    }

    public void set(Character c, int occurrences) {
        frequencies.put(c, occurrences);
    }

    public void decrease(Character c) {
        if (!frequencies.containsKey(c)) {
            throw new IllegalArgumentException("Character " + c + " does not exist");
        }
        int count = frequencies.get(c);
        if (count == 0) {
            throw new IllegalArgumentException("Character " + c + " already has zero occurences");
        }
        frequencies.put(c, --count);
    }

    public int occurrences(Character c) {
        return frequencies.getOrDefault(c, 0);
    }

    public Map<Character, Integer>  getFrequencies() {
        return frequencies;
    }
}
