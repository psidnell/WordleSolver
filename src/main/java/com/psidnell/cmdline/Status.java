package com.psidnell.cmdline;

import com.psidnell.Wordle;

import java.util.List;

public class Status implements Command {

    private final Wordle wordle;

    public Status(Wordle wordle) {
        this.wordle = wordle;
    }

    @Override
    public boolean accepts(String commandName, List<String> arguments) {
        return ("s".equals(commandName) || "status".equals(commandName)) && arguments.isEmpty();
    }

    @Override
    public boolean execute(List<String> arguments) {
        wordle.printStatus();
        return true;
    }

    @Override
    public String getName() {
        return "Status";
    }

    @Override
    public String commandHelp() {
        return "s/status | print game status";
    }
}
