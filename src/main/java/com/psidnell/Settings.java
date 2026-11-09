package com.psidnell;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class Settings {

    private final Properties properties;
    private static Settings instance = null;

    private Settings() {
        properties = loadProperties();
    }

    private Properties loadProperties()  {
        Properties result = new Properties();
        try (InputStream inputStream = Settings.class.getResourceAsStream("/com/psidnell/settings.properties")) {
            result.load(inputStream);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Settings getInstance() {
        synchronized (Settings.class) {
            if (instance == null) {
                instance = new Settings();
            }
            return instance;
        }
    }

    public System.Logger.Level getLogLevel() {
        return System.Logger.Level.valueOf(properties.getProperty("LogLevel", "DEBUG"));
    }

    public int getSearchThreshold1() {
        return Integer.parseInt(properties.getProperty("SearchThreshold1", "1000"));
    }

    public int getSearchThreshold2() {
        return Integer.parseInt(properties.getProperty("SearchThreshold2", "500"));
    }

    public int getSearchThreshold3() {
        return Integer.parseInt(properties.getProperty("SearchThreshold3", "250"));
    }
}
