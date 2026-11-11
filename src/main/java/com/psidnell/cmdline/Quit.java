package com.psidnell.cmdline;

import java.util.List;

public class Quit implements Command {

    @Override
    public boolean accepts(String commandName, List<String> arguments) {
        return ("q".equals(commandName) || "quit".equals(commandName)) && arguments.isEmpty();
    }

    @Override
    public boolean execute(List<String> arguments) {
        return false;
    }

    @Override
    public String getName() {
        return "Quit";
    }

    @Override
    public String commandHelp() {
        return "q/quit | quit";
    }
}
