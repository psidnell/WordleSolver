package com.psidnell.log;

import com.psidnell.Settings;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ConsoleLogger implements System.Logger {

    private final String loggerName;
    private final Settings settings = Settings.getInstance();
    private final System.Logger.Level loggerLevel;

    public ConsoleLogger(String name, Module module) {
        this.loggerName = name;
        this.loggerLevel = settings.getLogLevel();
    }

    @Override
    public String getName() {
        return "ConsoleLogger";
    }

    @Override
    public boolean isLoggable(Level level) {
        return true;
    }

    @Override
    public void log(Level msgLevel, ResourceBundle bundle, String msg, Throwable thrown) {
        if (shouldLogMessage(msgLevel)) {
            System.out.printf("%s [%s]: %s - %s%n", loggerName, msgLevel, msg, thrown);
            System.out.println(Arrays.toString(thrown.getStackTrace()));

        }
    }

    @Override
    public void log(Level msgLevel, ResourceBundle bundle, String format, Object... params) {
        if (shouldLogMessage(msgLevel)) {
            System.out.printf("%s [%s]: %s%n", loggerName, msgLevel,
                    MessageFormat.format(format, params));
        }
    }

    private boolean shouldLogMessage(Level msgLevel) {
        return loggerLevel.compareTo(msgLevel) <= 0;
    }
}