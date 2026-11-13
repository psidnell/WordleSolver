package com.psidnell;

import com.psidnell.cmdline.Command;
import com.psidnell.cmdline.Enter;
import com.psidnell.cmdline.Guess;
import com.psidnell.cmdline.Help;
import com.psidnell.cmdline.Quit;
import com.psidnell.cmdline.ShowWords;
import com.psidnell.cmdline.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final ApplicationContext context;
    private final Wordle wordle;

    private Main() throws Exception {
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        this.wordle = context.getBean("wordle", Wordle.class);
        String logLevel = context.getEnvironment().getProperty("LogLevel");
        com.psidnell.log.ConsoleLogger.setLogLevel(System.Logger.Level.valueOf(logLevel));
    }

    static void main(String[] args) throws Exception {

        new Main().processCommandLine();
    }

    void processCommandLine() {

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

    private boolean processLine(String input) {

        Help help = new Help();
        Command[] commands = new Command[]{
                help,

                new Status(wordle),
                new Enter(wordle),
                new Guess(wordle),
                new Status(wordle),
                new ShowWords(wordle),
                new Quit()
        };
        help.setCommands(commands);


        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        // Extract the first word as the command
        String[] parts = input.trim().split("\\s+", 3);
        String commandName = parts[0].toLowerCase();
        List<String> arguments = new LinkedList<>(Arrays.asList(parts));
        arguments.removeFirst();

        for (Command command : commands) {
            if (command.accepts(commandName, arguments)) {
                return command.execute(arguments);
            }
        }

        IO.println("Unknown command: " + input);
        help.execute(arguments);

        return true;
    }
}
