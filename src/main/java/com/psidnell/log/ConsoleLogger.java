package com.psidnell.log;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ConsoleLogger implements System.Logger {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ");

    private final String loggerName;
    private final System.Logger.Level loggerLevel;

    public ConsoleLogger(String name, Module module) {
        this.loggerName = name;
        this.loggerLevel = LoggerFinder.getLogLevel();
    }

    @Override
    public String getName() {
        return "ConsoleLogger";
    }

    @Override
    public boolean isLoggable(Level level) {
        return true;
    }


    private String time() {
        LocalDateTime myDateObj = LocalDateTime.now();
        return myDateObj.format(DATE_FORMAT);
    }

    @Override
    public void log(Level msgLevel, ResourceBundle bundle, String msg, Throwable thrown) {
        if (shouldLogMessage(msgLevel)) {
            System.out.print(time());
            System.out.printf("%s [%s]: %s - %s%n", loggerName, msgLevel, msg, thrown);
            System.out.println(Arrays.toString(thrown.getStackTrace()));

        }
    }

    @Override
    public void log(Level msgLevel, ResourceBundle bundle, String format, Object... params) {
        if (shouldLogMessage(msgLevel)) {
            System.out.print(time());
            System.out.printf("%s [%s]: %s%n", loggerName, msgLevel,
                    MessageFormat.format(format, params));
        }
    }

    private boolean shouldLogMessage(Level msgLevel) {
        return loggerLevel.compareTo(msgLevel) <= 0;
    }
}