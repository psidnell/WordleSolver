package com.psidnell.ruleset.util;

import com.psidnell.util.CharacterSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharacterSetTest {

    @Test
    public void testAdd() {
        CharacterSet set = new CharacterSet();
        assertFalse(set.contains('A'));
        set.add('A');
        assertTrue(set.contains('A'));
    }

    @Test
    public void testToString() {
        CharacterSet set = new CharacterSet();
        assertEquals ("", set.toString());
        set.add('A');
        set.add('Z');
        set.add('A');
        assertEquals ("AZ", set.toString());
    }

    @Test
    public void testForEach() {
        CharacterSet set = new CharacterSet();
        assertEquals ("", set.toString());
        set.add('A');
        set.add('B');
        StringBuilder b = new StringBuilder();
        set.forEach(c -> b.append('(').append(c).append(')'));
        assertEquals("(A)(B)", b.toString());
    }
}
