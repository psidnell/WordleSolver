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
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();
    }

    @Test
    public void testImmediateSuccess() throws Exception {
        Context context = new Context();
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertTrue(wordle.userGuess("geeky"));
    }

    @Test
    public void testGuessOnLastTry() throws Exception {
        Context context = new Context();
        Wordle wordle = context.get("wordle", Wordle.class);
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
        Wordle wordle = context.get("wordle", Wordle.class);
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

    //todo

//    private static Stream<Arguments> provideSolutions() {
//        return Stream.of(
//                // Solution, guesses
//                //Arguments.of("grist", new String[]{}),
//                Arguments.of("grist", new String[]{"trace", "misty"}),
//                Arguments.of("align",  new String[]{"trace"}),
//                Arguments.of("aloft", new String[]{"trace"}),
//                Arguments.of("grump", new String[]{"trace"}),
//                Arguments.of("geeky", new String[]{"trace"})
//        );
//    }
//    @ParameterizedTest
//    @MethodSource("provideSolutions")
//    void testSolving(String solution, String[] guesses) throws Exception {
//        Context context = new Context();
//        Wordle wordle = context.get("wordle", Wordle.class);
//        wordle.setContext(context);
//        wordle.init();
//        wordle.setSolution(solution);
//
//        for (String guess : guesses) {
//            wordle.userGuess(guess);
//        }
//
//        solve(wordle);
//    }

    @Test
    public void testWithSmallDictionary() throws Exception {
        String[] words = {
                "angle", "apple", "ample", "ankle", "aztec",
                "bable", "bible", "bongo", "bilbo", "bungy",
                "cable", "cocky", "ching", "chive", "chirp",
                "dishy", "dirty", "dozer", "dozed", "drips",
                "ebene", "ebike", "ebons", "ebook", "ecads",
                "faker", "fakes", "fakie", "fakir", "falaj",
                "galah", "galas", "galax", "galea", "gales",
                "hated", "hates", "hatha", "hangs", "hatty",
        };
        Dictionary dictionary = new Dictionary(words);
        Context context = new Context(dictionary, new Wordle());
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("hated");
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
