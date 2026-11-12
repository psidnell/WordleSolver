package com.psidnell.cmdline;

import com.psidnell.Wordle;

import java.util.List;

public class Guess implements Command {

    private final Wordle wordle;

    public Guess(Wordle wordle) {
        this.wordle = wordle;
    }

    @Override
    public boolean accepts(String commandName, List<String> arguments) {
        return ("g".equals(commandName) || "guess".equals(commandName)) &&
                arguments.isEmpty();
    }

    @Override
    public boolean execute(List<String> arguments) {
        String bestWord = wordle.propose();
        System.out.println("Proposal: " + bestWord);
        return true;
    }

    @Override
    public String getName() {
        return "Guess";
    }

    @Override
    public String commandHelp() {
        return "g/guess | guess the next word";
    }
}
