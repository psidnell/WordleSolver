package com.psidnell.log;

public class LoggerFinder extends System.LoggerFinder {

    private static System.Logger.Level level = System.Logger.Level.INFO;

    // Must have a public zero-argument constructor
    public LoggerFinder() {
    }

    public static System.Logger.Level getLogLevel() {
        return level;
    }

    public static void setLogLevel(System.Logger.Level logLevel) {
        LoggerFinder.level = logLevel;
    }

    @Override
    public System.Logger getLogger(String name, Module module) {
        // Return your custom implementation of java.lang.System.Logger
        return new ConsoleLogger(name, module);
    }
}
