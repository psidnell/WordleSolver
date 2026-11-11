package com.psidnell.cmdline;

import com.psidnell.Wordle;

import java.util.List;

public class ShowWords implements Command {

    private final Wordle wordle;

    public ShowWords(Wordle wordle) {
        this.wordle = wordle;
    }

    @Override
    public boolean accepts(String commandName, List<String> arguments) {
        return ("w".equals(commandName) || "words".equals(commandName)) && arguments.isEmpty();
    }

    @Override
    public boolean execute(List<String> arguments) {
        wordle.showRemaining();
        return true;
    }

    @Override
    public String getName() {
        return "Show Words";
    }

    @Override
    public String commandHelp() {
        return "w/words | show remaining possible words";
    }
}
