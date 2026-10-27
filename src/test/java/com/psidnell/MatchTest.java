package com.psidnell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchTest
{
    @Test
    public void testGuessRightPositions() {
        Word solution =  new Word("geeky");
        Word guess = new Word("lanky");

        Match match = new Match(guess, solution);
        assertEquals("LANKY LX AX NX KM YM K>=1 Y>=1", match.toString());
    }

    @Test
    public void testGuessWrongPositions() {
        Word solution =  new Word("geeky");
        Word guess = new Word("egyee");

        Match match = new Match(guess, solution);
        assertEquals("EGYEE E? G? Y? E? EX E>=2 G>=1 Y>=1", match.toString());
    }

    @Test
    public void testRightAndWrongPositionsWithWrongFirst() {
        Word solution =  new Word("geeky");
        Word guess = new Word("egeey");

        Match match = new Match(guess, solution);
        assertEquals("EGEEY E? G? EM EX YM E>=2 G>=1 Y>=1", match.toString());
    }


}
