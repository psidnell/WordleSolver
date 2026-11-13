package com.psidnell.log;

public class LoggerFinder extends System.LoggerFinder {


    // Must have a public zero-argument constructor
    public LoggerFinder() {
    }

    @Override
    public System.Logger getLogger(String name, Module module) {
        // Return your custom implementation of java.lang.System.Logger
        return new ConsoleLogger(name, module);
    }
}
