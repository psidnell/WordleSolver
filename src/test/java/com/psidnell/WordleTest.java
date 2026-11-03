package com.psidnell;

import com.psidnell.rules.Rule;
import com.psidnell.util.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
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
                Wordle.FIRST_GUESS // Must be in dictionary!
        };
        Dictionary dictionary = new Dictionary(words);
        Context context = new Context(dictionary, new Wordle());
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("hated");
        solve(wordle);
    }

    //todo try and solve ALL dictionary words - when performance improved
    private static Stream<Arguments> provideSolutions() {
        return Stream.of(
                // Solution, guesses
                Arguments.of("legno", 4, new String[]{}),
                Arguments.of("pouch", 4, new String[]{}),
                Arguments.of("scoot", 3, new String[]{}),
                Arguments.of("viper", 5, new String[]{}),
                Arguments.of("yahoo", 5, new String[]{}),
                Arguments.of("grist", 4, new String[]{}),
                Arguments.of("align", 3, new String[]{}),
                Arguments.of("aloft", 3, new String[]{}),
                Arguments.of("grump", 4, new String[]{}),
                Arguments.of("geeky", 5, new String[]{}),
                Arguments.of("slide", 4, new String[]{}),
                Arguments.of("motor", 4, new String[]{})
        );
    }
    @ParameterizedTest
    @MethodSource("provideSolutions")
    void testSolvingInFixedTurns(String solution, int tries, String[] guesses) throws Exception {
        Context context = new Context();
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();
        wordle.setSolution(solution);

        for (String guess : guesses) {
            wordle.userGuess(guess);
        }

        solve(wordle);
        assertEquals(tries, wordle.guessesMade);
    }

    @Test
    void testSolvingRandomWords() throws Exception {

        for (int i = 0; i < 5; i++) {
            Context context = new Context();
            Wordle wordle = context.get("wordle", Wordle.class);
            wordle.setContext(context);
            wordle.init();
            Dictionary dictionary = context.get("dictionary", Dictionary.class);
            Word[] dictWords = dictionary.getWords().toArray(new Word[0]);
            Random random = new Random();
            Word randomWord = dictWords[random.nextInt(dictWords.length)];
            wordle.setSolution(randomWord.getWord());
            solve(wordle);
        }
    }

    @Test
    public void testPlayRealGame() throws Exception {
        Context context = new Context();
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();

        wordle.answer("trace", "___C_");
        wordle.printStatus();
        assertFalse(wordle.isSolved());
        wordle.answer("slink", "_____");
        wordle.printStatus();
        wordle.showRemaining();
        wordle.answer("pouch", "POUCH");
        assertTrue(wordle.isSolved());
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
