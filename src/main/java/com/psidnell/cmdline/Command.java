package com.psidnell.cmdline;

import java.util.List;

public interface Command {

    boolean accepts(String commandName, List<String> arguments);

    boolean execute(List<String> arguments);

    String getName();

    String commandHelp();
}
