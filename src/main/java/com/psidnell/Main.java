package com.psidnell;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {

    private static Wordle wordle;

    public static void main(String[] args) throws Exception {
       final Context context = new Context();
       wordle = context.get("wordle", Wordle.class);
       wordle.setSolution("sleep");

        IO.println("Wordle Solver");

        // Interactive terminal loop
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            wordle.printStatus();

            IO.print("Enter command: ");
            String input = scanner.nextLine();

            running = processLine(input, running);
        }

        scanner.close();
    }

    private static boolean processLine(String input, boolean running) {
        if (input == null || input.trim().isEmpty()) {
            return running;
        }

        // Extract the first word as the command
        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "q":
            case "quit":
                IO.println("Goodbye!");
                running = false;
                break;
            case "guess":
                wordle.guess(parts[1].toLowerCase());
                break;
            default:
                IO.println("Unknown command: " + command);
                IO.println("Type 'help' for available commands.");
                break;
        }

        return running;
    }
}
