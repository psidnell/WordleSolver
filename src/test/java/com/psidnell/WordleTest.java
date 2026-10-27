package com.psidnell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordleTest {

    @Test
    public void testConstruction() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();
    }

    @Test
    public void testImmediateSuccess() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertTrue(wordle.userGuess("geeky"));
    }

    @Test
    public void testGuessOnLastTry() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertTrue(wordle.userGuess("geeky"));
    }

    @Test
    public void testTooManyGuesses() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("wonky"));
        assertFalse(wordle.userGuess("geeky"));
    }

    @Test
    public void testScenario() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertFalse(wordle.userGuess("woody"));
        assertFalse(wordle.userGuess("leaky"));
        assertFalse(wordle.userGuess("spear"));
        assertFalse(wordle.userGuess("apgar"));
        assertTrue(wordle.userGuess(wordle.propose()));
        wordle.printStatus();
        wordle.showRemaining();
        wordle.propose();
    }

    @Test
    public void testProposal() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("bingo");
        //assertFalse(wordle.userGuess("trace"));
        //assertFalse(wordle.userGuess("spoil"));
        //assertFalse(wordle.userGuess("brink"));
        //assertFalse(wordle.userGuess("woody"));
        //assertFalse(wordle.userGuess("leaky"));
        //assertFalse(wordle.userGuess("lanky"));
        wordle.printStatus();
        String proposal = wordle.propose();
        while (wordle.getGuessesRemaining() > 0 && !wordle.userGuess(proposal)) {
            wordle.printStatus();
            proposal = wordle.propose();
        }
        wordle.printStatus();
        assertTrue(wordle.getGuessesRemaining() > 0);
    }
}
