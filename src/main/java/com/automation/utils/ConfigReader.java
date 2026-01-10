package com.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = loadProperties();

    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            // If properties file can't be read, we'll rely on system properties and defaults.
            System.err.println("Warning: could not load config.properties: " + e.getMessage());
        }
        return p;
    }

    private static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isEmpty()) {
            return sys;
        }
        return props.getProperty(key, defaultValue);
    }

    public static String getBaseUrl() {
        return get("baseUrl", "https://automationexercise.com");
    }
}

