package com.psidnell;

public class Wordle extends ComponentImpl {

    private Word solution;
    int guessesRemaining = 6;
    private Solutions solutions;

    @Override
    public void init() {
        super.init();
        this.solutions = new Solutions(dictionary);
    }

    public void setSolution(String solution) {
        if (!dictionary.hasWord(solution)) {
            throw new IllegalArgumentException("solution is not a valid word");
        }
        this.solution = new Word (solution);
    }

    public void printStatus() {
        System.out.println("Solutions: " + solutions.getSolutions().size());
        System.out.println("Guesses Remaining: " + guessesRemaining);
    }

    // TODO Move all of the tests snd printing here
    public boolean userGuess(String guess) {

        if (guessesRemaining == 0) {
            System.out.println("FAILED! No more guesses!");
            return false;
        }

        Word guessWord = new Word(guess);

        if (guess.length() != 5) {
            System.out.println("Guess must be 5 characters");
            return false;
        }

        if (!dictionary.hasWord(guess)) {
            System.out.println("Unknown word");
            return false;
        }

        // We have a valid guess
        guessesRemaining--;

        Match match = guess(guessWord, solution, solutions);
        System.out.println(match);


        if (match.isMatch()) {
            System.out.println("SUCCESS Guessed correctly in " + (6 - guessesRemaining) + " guesses" );
            return true;
        }

        return false;
    }

    public Match guess(Word guess, Word solution, Solutions solutions) {


        Match match = new Match(guess, solution);

        solutions.prune(match);

        return match;
    }

    public void showRemaining() {
        for (Word word : solutions.getSolutions()) {
            System.out.println(word);
        }
    }

    public String propose() {
        // Create a new wordle instance and guess with every dictionary word
        // Remember the one with the smallest resultant solutions number

        // Shouldn't be necessary
        if (solutions.getSolutions().size() == 1) {
            return solutions.getSolutions().stream().findFirst().get().getWord();
        }

        int minSolutionSize = Integer.MAX_VALUE;
        Word bestWord = null;

        int dictWords = dictionary.getWords().size();
        int wordsDone = 0;
        int percentDone = -1;
        for (Word dictionaryGuess: dictionary.getWords().values()) {

            //System.out.println("Best: " + bestWord + " Trying: " + dictionaryGuess);

            int totalSolutionSizeForGuess = 0;
            int startSolutionSize = solutions.getSolutions().size();
//            int totalImprovementsForGuess = 0 ;
//            int maxImprovementsForGuess = 0;

            for (Word posSolution : solutions.getSolutions()) {
                // Can I bail early if words don't overlap?
                // Put this condition around the rest?
//                if (!dictionaryGuess.haveCommonCharacters(posSolution)) {
//                    // If the words have nothing in common then we would learn nothing from guess
//                    //System.out.println("No Overlap: " + dictionaryGuess + "/" + posSolution);
//                    continue;
//                }

                Solutions nextSolutions = new Solutions(solutions);
                guess(dictionaryGuess, posSolution, nextSolutions);
                int guessSolutionsSize = nextSolutions.getSolutions().size();
//                if (guessSolutionsSize < startSolutionSize) {
//                    totalImprovementsForGuess++;
//                    //System.out.println("Improvement: " + dictionaryGuess);
//                }

                totalSolutionSizeForGuess += guessSolutionsSize;
//                if (totalSolutionSizeForGuess > minSolutionSize) {
//                    // If we're never going to get under the minimum permutations
//                    // for this guess then abort
//                    //System.out.println("Too many solutions: " + dictionaryGuess + "/" + posSolution + " solutions: " + totalSolutionSizeForGuess);
//                    totalSolutionSizeForGuess = minSolutionSize + 1;
//                    break;
//

                //System.out.println(totalSolutionSizeForGuess);
            }

            //System.out.println("Analysed: " + dictionaryGuess + " solutions: " + totalSolutionSizeForGuess);

            if (totalSolutionSizeForGuess < minSolutionSize) {
                System.out.println("Best word: " + dictionaryGuess + " solutions: " + totalSolutionSizeForGuess +
                        " dropped: " + (minSolutionSize - totalSolutionSizeForGuess));
                minSolutionSize = totalSolutionSizeForGuess;
                bestWord = dictionaryGuess;
            }

            wordsDone++;
            int nextPercentDome = (100 * wordsDone) / dictWords;
            if (percentDone != nextPercentDome) {
                percentDone = nextPercentDome;
                //System.out.println("PercentDone: " + percentDone);
            }

        }

        System.out.println("Best word: " + bestWord + " solutions: " + minSolutionSize);
        return bestWord.getWord();
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }
}
