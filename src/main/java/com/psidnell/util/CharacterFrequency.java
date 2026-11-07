package com.psidnell.util;

import com.psidnell.words.Word;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

// TODO Can I improve on this?
public class CharacterFrequency {
    private final int[] counts = new int[26];

    public CharacterFrequency() {
    }

    public CharacterFrequency(Word word) {
        String word1 = word.getWord();
        for (int i = 0; i < 5; i++) {
            char ch = word1.charAt(i);
            if (Character.isAlphabetic(ch)) {
                increase(ch);
            }
        }
    }

    public CharacterFrequency(CharacterFrequency o) {
        for (int i = 0; i < counts.length; i++) {
            counts[i] = o.counts[i];
        }
    }

    private static int indexOf(Character ch) {
        return ch - 'A';
    }

    private static char characterFor(int i) {
        return (char) ('A' + i);
    }

    public void increase(Character c) {
        counts[indexOf(c)]++;
    }

    public void set(Character c, int occurrences) {
        counts[indexOf(c)] = occurrences;
    }

    public void decrease(Character c) {
        int i = indexOf(c);
        if (counts[i] == 0) {
            throw new IllegalArgumentException("Character " + c + " already has zero occurences");
        }

        counts[i]--;
    }

    public int occurrences(Character ch) {
        return counts[indexOf(ch)];
    }

    public Map<Character, Integer> frequencies() {
        TreeMap<Character, Integer> result = new TreeMap<>();
        forEach(result::put);
        return result;
    }

    public int overlapSize() {
        AtomicInteger sum = new AtomicInteger(0);
        forEach((ch, count) -> sum.addAndGet(count));
        return sum.get();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        final AtomicBoolean first = new AtomicBoolean(true);

        forEach((ch, count) -> {
            if (!first.get()) {
                sb.append(',');
            }
            sb.append(ch).append(':').append(count);
            first.set(false);
        });

        sb.append('}');
        return sb.toString();
    }

    public void forEach(BiConsumer<? super Character, ? super Integer> c) {
        for (int i = 0; i < counts.length; i++) {
            int count = counts[i];
            if (count > 0) {
                Character ch = characterFor(i);
                c.accept(ch, count);
            }
        }
    }
}
