package com.psidnell.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class CharacterSet {
    // TODO Can I speed this up?
    private Character[] chars = new Character[26];

    public CharacterSet() {
    }

    public CharacterSet(CharacterSet chars) {
        addAll(chars);
    }

    private int indexOf(Character ch) {
        return ch - 'A';
    }

    public void add(Character ch) {
        chars[indexOf(ch)] = ch;
    }

    public void addAll(CharacterSet o) {
        o.forEach(this::add);
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        forEach(b::append);
        return b.toString();
    }

    public void forEach(Consumer<? super Character> c) {
        for (int i = 0; i < this.chars.length; i++) {
            Character ch = chars[i];
            if (ch != null) {
                c.accept(ch);
            }
        }
    }

    public Collection<Character> values() {
        List<Character> result = new LinkedList<>();
        forEach(result::add);
        return result;
    }

    public boolean contains(Character ch) {
        return chars[indexOf(ch)] != null;
    }
}
