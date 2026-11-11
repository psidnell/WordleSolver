package com.psidnell.cmdline;

import com.psidnell.Wordle;

import java.util.List;

public class Enter implements Command {

    private final Wordle wordle;

    public Enter(Wordle wordle) {
        this.wordle = wordle;
    }

    @Override
    public boolean accepts(String commandName, List<String> arguments) {
        return ("e".equals(commandName) || "enter".equals(commandName)) && arguments.isEmpty();
    }

    @Override
    public boolean execute(List<String> arguments) {
        return wordle.answer(arguments.get(0), arguments.get(1));
    }

    @Override
    public String getName() {
        return "Enter";
    }

    @Override
    public String commandHelp() {
        StringBuilder b = new StringBuilder();
        b.append("e/enter <guess word> <result> | enter an answer for the game(1)\n");
        b.append("(1): result is \"Xx___\" where \"X\" is correct and \"x\" is the wrong place");
        return b.toString();
    }
}
