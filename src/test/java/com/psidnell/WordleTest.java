package com.psidnell;

import com.psidnell.profile.Profiler;
import com.psidnell.solution.EarlyGuessSolutionEvaluator;
import com.psidnell.words.Dictionary;
import com.psidnell.words.Word;
import com.psidnell.words.WordList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class WordleTest {

    @Test
    public void testConstruction() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        Wordle wordle = context.getBean("wordle", Wordle.class);
        assertNotNull(wordle);
    }

    @Test
    public void testImmediateSuccess() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        Wordle wordle = context.getBean("wordle", Wordle.class);

        wordle.setSolution("GEEKY");
        assertTrue(wordle.guess("GEEKY"));
    }

    @Test
    public void testGuessOnLastTry() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        Wordle wordle = context.getBean("wordle", Wordle.class);

        wordle.setSolution("geeky");
        assertFalse(wordle.guess("pinky"));
        assertFalse(wordle.guess("binky"));
        assertFalse(wordle.guess("lanky"));
        assertFalse(wordle.guess("sinky"));
        assertFalse(wordle.guess("dinky"));
        assertTrue(wordle.guess("geeky"));
    }

    @Test
    public void testTooManyGuesses() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        Wordle wordle = context.getBean("wordle", Wordle.class);

        wordle.setSolution("geeky");
        assertFalse(wordle.guess("pinky"));
        assertFalse(wordle.guess("binky"));
        assertFalse(wordle.guess("lanky"));
        assertFalse(wordle.guess("sinky"));
        assertFalse(wordle.guess("dinky"));
        assertFalse(wordle.guess("linky"));
        try {
            assertFalse(wordle.guess("geeky"));
            fail();
        } catch (PlayerException _) {
        }
    }

    static class SmallDictionaryApplicationContext extends ApplicationConfiguration {
        static final String[] words = {
                "angle", "apple", "ample", "ankle", "aztec",
                "bable", "bible", "bongo", "bilbo", "bungy",
                "cable", "cocky", "ching", "chive", "chirp",
                "dishy", "dirty", "dozer", "dozed", "drips",
                "ebene", "ebike", "ebons", "ebook", "ecads",
                "faker", "fakes", "fakie", "fakir", "falaj",
                "galah", "galas", "galax", "galea", "gales",
                "hated", "hates", "hatha", "hangs", "hatty",
                EarlyGuessSolutionEvaluator.FIRST_GUESS, // Must be in dictionary!
        };

        //@Bean
        WordList dictionary() throws IOException {
            return new Dictionary(words);
        }
    }
    @Test
    public void testWithSmallDictionary() throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(SmallDictionaryApplicationContext.class);
        Wordle wordle = context.getBean("wordle", Wordle.class);

        wordle.setSolution("hated");
        solve(wordle, true);
    }

    //todo try and solve ALL dictionary words - when performance improved
    private static Stream<Arguments> provideSolutions() {
        return Stream.of(
                // Solution, guesses, succeeds

                // Successes
                Arguments.of("moxie", 5, new String[]{}, true),
                Arguments.of("karat", 3, new String[]{}, true),
                Arguments.of("bless", 3, new String[]{}, true),
                Arguments.of("truce", 3, new String[]{}, true),
                Arguments.of("loamy", 4, new String[]{}, true),
                Arguments.of("wangs", 6, new String[]{}, true),
                Arguments.of("waxes", 6, new String[]{}, true),
                Arguments.of("first", 3, new String[]{}, true),
                Arguments.of("prate", 3, new String[]{}, true),
                Arguments.of("soyle", 4, new String[]{}, true),
                Arguments.of("dreck", 2, new String[]{}, true),
                Arguments.of("parch", 4, new String[]{}, true),
                Arguments.of("nutty", 5, new String[]{}, true),
                Arguments.of("noggs", 4, new String[]{}, true),
                Arguments.of("legno", 4, new String[]{}, true),
                Arguments.of("pouch", 4, new String[]{}, true),
                Arguments.of("scoot", 3, new String[]{}, true),
                Arguments.of("viper", 5, new String[]{}, true),
                Arguments.of("yahoo", 4, new String[]{}, true),
                Arguments.of("grist", 3, new String[]{}, true),
                Arguments.of("align", 4, new String[]{}, true),
                Arguments.of("aloft", 3, new String[]{}, true),
                Arguments.of("grump", 4, new String[]{}, true),
                Arguments.of("geeky", 4, new String[]{}, true),
                Arguments.of("slide", 4, new String[]{}, true),
                Arguments.of("motor", 4, new String[]{}, true),

                // Falures
                Arguments.of("nunks", 6, new String[]{}, false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideSolutions")
    void testSolvingInFixedTurns(String solution, int tries, String[] guesses, boolean succeeds) throws Exception {
        try {
            Profiler.enable(false);
            ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
            Wordle wordle = context.getBean("wordle", Wordle.class);
            wordle.setSolution(solution);

            for (String guess : guesses) {
                wordle.guess(guess);
            }

            solve(wordle, succeeds);
            assertEquals(tries, wordle.getState().getGuessesMade());
        } finally {
            Profiler.report();
        }
    }

    @Test
    void testSolvingRandomWords() throws Exception {
        for (int i = 0; i < 10; i++) {
            ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
            Wordle wordle = context.getBean("wordle", Wordle.class);
            Dictionary dictionary = context.getBean("dictionary", Dictionary.class);
            Word[] dictWords = dictionary.getWords().toArray(new Word[0]);
            Random random = new Random();
            Word randomWord = dictWords[random.nextInt(dictWords.length)];
            wordle.setSolution(randomWord.getWord());
            solve(wordle, true);
        }
    }

    @Test
    public void testPlayRealGame() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        Wordle wordle = context.getBean("wordle", Wordle.class);

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
            assertTrue(wordle.getState().getGuessesMade() <= 6);
        } else {
            assertEquals(6, wordle.getState().getGuessesMade());
        }
    }
}
