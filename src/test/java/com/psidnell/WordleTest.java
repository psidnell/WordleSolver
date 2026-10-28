package com.psidnell;

import com.psidnell.rules.Rule;
import com.psidnell.util.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testScenario1() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertFalse(wordle.userGuess("woody"));
        assertFalse(wordle.userGuess("leaky"));
        assertFalse(wordle.userGuess("spear"));
        assertFalse(wordle.userGuess("apgar"));
        solve(wordle);
    }

    @Test
    public void testScenario2() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("grist");
        assertFalse(wordle.userGuess("trace"));
        assertFalse(wordle.userGuess("swing"));
        //assertFalse(wordle.userGuess("trace"));
        //assertFalse(wordle.userGuess("spoil"));
        //assertFalse(wordle.userGuess("brink"));
        //assertFalse(wordle.userGuess("woody"));
        //assertFalse(wordle.userGuess("leaky"));
        //assertFalse(wordle.userGuess("lanky"));
        solve(wordle);
    }

    @Test
    public void testScenario3() throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();
        Solutions solutions = wordle.getSolutions();

        wordle.setSolution("grist");
        //assertFalse(wordle.userGuess("trace"));
//        assertEquals(43, solutions.getSolutions().size());
//        wordle.propose();

        // Why is the result (43) so much worse that my guess of swing that leaves 9?
        //wordle.printStatus();


        //assertFalse(wordle.userGuess("swing"));
        //assertEquals(2, solutions.getSolutions().size());

        solve(wordle);

    }

    private static Stream<Arguments> provideSolutions() {
        return Stream.of(
                Arguments.of("grist"),
                Arguments.of("align")
                //Arguments.of("aloft")
                //Arguments.of("grump")
        );
    }
    @ParameterizedTest
    @MethodSource("provideSolutions")
    void testSolving(String solution) throws Exception {
        Context context = new Context();
        Wordle wordle = new Wordle();
        wordle.setContext(context);
        wordle.init();
        wordle.setSolution(solution);
        solve(wordle);
    }

    private static void solve(Wordle wordle) {
        while (wordle.getGuessesRemaining() > 0 && !wordle.isSolved()) {
            String proposal = wordle.propose();
            wordle.userGuess(proposal);
            wordle.printStatus();
        }
        wordle.printStatus();
        assertTrue(wordle.isSolved());
    }
}
