package com.psidnell.rules;

import com.psidnell.words.Word;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public record CharacterFrequency(Map<Character, Integer> frequencies) {
    public CharacterFrequency(Word word) {
        this(word.getWord());
    }

    public CharacterFrequency(String word) {
        this(new TreeMap<>());
        for (int i = 0; i < 5; i++) {
            char ch = word.charAt(i);
            if (Character.isAlphabetic(ch)) {
                increase(ch);
            }
        }
    }

    public CharacterFrequency() {
        this(new TreeMap<>());
    }

    public CharacterFrequency(CharacterFrequency other) {
        this(new TreeMap<>(other.frequencies));
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

    @Override
    public Map<Character, Integer> frequencies() {
        return Collections.unmodifiableMap(frequencies);
    }


    public int overlapSize() {
        return frequencies.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append('{');
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            sb.append(entry.getKey()).append(':').append(entry.getValue());
            first = false;
        }
        sb.append('}');
        return sb.toString();
    }
}
