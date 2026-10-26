package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IO.println("Wordle Solver");

        // Interactive terminal loop
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
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
            case "help":
                IO.println("Available commands:");
                IO.println("  quit - Exit the program");
                IO.println("  help - Show this help message");
                IO.println("  solve - Start solving a wordle puzzle");
                break;
            case "solve":
                if (parts.length > 1) {
                    IO.println("Solving with pattern: " + parts[1]);
                } else {
                    IO.println("Solving wordle puzzle...");
                }
                break;
            default:
                IO.println("Unknown command: " + command);
                IO.println("Type 'help' for available commands.");
                break;
        }
        return running;
    }
}
