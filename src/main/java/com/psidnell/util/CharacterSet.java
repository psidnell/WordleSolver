package com.psidnell.util;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

public class CharacterSet {
    // TODO Can I speed this up?
    private TreeSet<Character> chars;

    public CharacterSet() {
        chars = new TreeSet<>();
    }

    public CharacterSet(CharacterSet chars) {
        this.chars = new TreeSet<>(chars.chars);
    }

    public void add(Character ch) {
        chars.add(ch);
    }

    public void addAll(CharacterSet chars) {
        this.chars.addAll(chars.chars);
    }

    public String toString() {
        StringBuilder b = new StringBuilder(chars.size());
        chars.forEach(b::append);
        return b.toString();
    }

    public void forEach(Consumer<? super Character> c) {
        chars.forEach(c);
    }

    public Set<Character> values() {
        return chars;
    }

    public boolean contains(Character ch) {
        return chars.contains(ch);
    }
}
