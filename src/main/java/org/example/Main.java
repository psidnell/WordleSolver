package org.example;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Wordle Solver"));

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
        switch (input.toLowerCase()) {
            case "q":
            case "quit":
                IO.println("Goodbye!");
                running = false;
                break;
            default:
                IO.println("You entered: " + input);
                // Add your custom processing logic here
                IO.println("Processing command...");
                break;
        }
        return running;
    }
}
