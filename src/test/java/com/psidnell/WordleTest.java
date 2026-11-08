package com.psidnell;

import com.psidnell.profile.Profiler;
import com.psidnell.util.Context;
import com.psidnell.words.Dictionary;
import com.psidnell.words.Word;
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

        wordle.setSolution("GEEKY");
        assertTrue(wordle.guess("GEEKY"));
    }

    @Test
    public void testGuessOnLastTry() throws Exception {
        Context context = new Context();
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertTrue(wordle.guess("geeky"));
    }

    @Test
    public void testTooManyGuesses() throws Exception {
        Context context = new Context();
        Wordle wordle = context.get("wordle", Wordle.class);
        wordle.setContext(context);
        wordle.init();

        wordle.setSolution("geeky");
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("wonky"));
        assertFalse(wordle.guess("geeky"));
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
        solve(wordle, true);
    }

    //todo try and solve ALL dictionary words - when performance improved
    private static Stream<Arguments> provideSolutions() {
        return Stream.of(
                // Solution, guesses, succeeds

                // Falures
                Arguments.of("wangs", 6, new String[]{}, false),
                Arguments.of("waxes", 6, new String[]{}, false),
                Arguments.of("nunks", 6, new String[]{}, false),

                 // Successes
                Arguments.of("first", 3, new String[]{}, true),
                Arguments.of("prate", 3, new String[]{}, true),
                Arguments.of("soyle", 4, new String[]{}, true),
                Arguments.of("dreck", 2, new String[]{}, true),
                Arguments.of("parch", 4, new String[]{}, true),
                Arguments.of("nutty", 3, new String[]{}, true),
                Arguments.of("noggs", 5, new String[]{}, true),
                Arguments.of("legno", 4, new String[]{}, true),
                Arguments.of("pouch", 4, new String[]{}, true),
                Arguments.of("scoot", 3, new String[]{}, true),
                Arguments.of("viper", 5, new String[]{}, true),
                Arguments.of("yahoo", 5, new String[]{}, true),
                Arguments.of("grist", 3, new String[]{}, true),
                Arguments.of("align", 3, new String[]{}, true),
                Arguments.of("aloft", 3, new String[]{}, true),
                Arguments.of("grump", 4, new String[]{}, true),
                Arguments.of("geeky", 4, new String[]{}, true),
                Arguments.of("slide", 4, new String[]{}, true),
                Arguments.of("motor", 4, new String[]{}, true)
        );
    }
    @ParameterizedTest
    @MethodSource("provideSolutions")
    void testSolvingInFixedTurns(String solution, int tries, String[] guesses, boolean succeeds) throws Exception {
        try {
            Profiler.enable(false);
            Context context = new Context();
            Wordle wordle = context.get("wordle", Wordle.class);
            wordle.setContext(context);
            wordle.init();
            wordle.setSolution(solution);

            for (String guess : guesses) {
                wordle.guess(guess);
            }

            solve(wordle, succeeds);
            assertEquals(tries, wordle.guessesMade);
        } finally {
            Profiler.report();
        }
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
            solve(wordle, true);
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

    private static void solve(Wordle wordle, boolean expectSuccess) {
        while (wordle.getGuessesRemaining() > 0 && !wordle.isSolved()) {
            String proposal = wordle.propose();
            wordle.guess(proposal);
            wordle.printStatus();
        }
        wordle.printStatus();
        assertEquals(expectSuccess, wordle.isSolved());
        if (expectSuccess) {
            assertTrue(wordle.guessesMade <= 6);
        } else {
            assertEquals(6, wordle.guessesMade);
        }
    }
}
