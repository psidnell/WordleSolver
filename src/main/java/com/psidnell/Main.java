package com.psidnell;

import com.psidnell.util.Context;

import java.util.Scanner;

public class Main {

    private static Wordle wordle;

    static void main(String[] args) throws Exception {
       final Context context = new Context();
       wordle = context.get("wordle", Wordle.class);

        IO.println("Wordle Solver");

        // Interactive terminal loop
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            try {
                IO.print("Enter command: ");
                String input = scanner.nextLine();

                running = processLine(input);
            } catch (PlayerException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static boolean processLine(String input) {

        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        // Extract the first word as the command
        String[] parts = input.trim().split("\\s+", 3);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "h":
            case "help":
                ensureArguments(parts, 0);
                printHelp();
                break;
            case "q":
            case "quit":
                ensureArguments(parts, 0);
                IO.println("Goodbye!");
                return false;
            case "s":
            case "solution":
                ensureArguments(parts, 1);
                wordle.setSolution(parts[1].toLowerCase());
                break;
            case "g":
            case "guess":
                ensureArguments(parts, 1);
                boolean correct = wordle.guess(parts[1].toLowerCase());
                boolean guessesRemaining = wordle.getGuessesRemaining() >0;
                return guessesRemaining && !correct;
            case "r":
            case "remaining":
                ensureArguments(parts, 0);
                wordle.showRemaining();
                break;
            case "p":
            case "propose":
                ensureArguments(parts, 0);
                String bsetWord = wordle.propose();
                System.out.println("Proposal: " + bsetWord);
                break;
            case "a":
            case "answer":
                ensureArguments(parts, 2);
                return wordle.answer(parts[1], parts[2]);
            default:
                IO.println("Unknown command: " + command);
                IO.println("Type 'help' for available commands.");
                break;
        }

        return true;
    }

    static void ensureArguments(String[] parts, int count) {
        if (parts.length - 1 != count) {
            throw new PlayerException("Wrong number of arguments, expected " + count);
        }
    }

    private static void printHelp() {
        System.out.println("Command Line Wordle Solver");
        System.out.println("Commands:");
        System .out.println("h/help | print this help");
        System .out.println("s/solution <solution word> | set a know solution");
        System.out.println("g/guess <guess word> | guess against a known solution");
        System.out.println("r/remaining | show remaining possible solutions");
        System.out.println("p/propose | propose the next word");
        System.out.println("a/answer <guess word> <result> | provide an answer for the real game(1)");
        System.out.println("q/quit | quit");
        System.out.println("(1): result is \"Xx___\" where \"X\" is correct and \"x\" is the wrong place");
    }
}
