package com.psidnell.cmdline;

import java.util.List;

public class Help implements Command {

    private Command[] commands;

    @Override
    public boolean accepts(String commandName, List<String> arguments) {
        return ("h".equals(commandName) || "help".equals(commandName)) && arguments.isEmpty();
    }

    @Override
    public boolean execute(List<String> arguments) {
        for (Command command : commands) {
            IO.print(command.getName() + ": ");
            IO.println(command.commandHelp());
        }
        return true;
    }

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    public String commandHelp() {
        return "h/help | print this help";
    }

    public void setCommands(Command[] commands) {
        this.commands = commands;
    }
}
