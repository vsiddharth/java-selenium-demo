package com.example.shop.selenium.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads test-config.properties and overrides any value with -D system properties.
 *
 * Resolution order: System property > properties file > built-in default.
 * Lets the same suite run from IDE (uses file) and CI (uses -Dapp.baseUrl=...).
 */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("test-config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load test-config.properties", e);
        }
    }

    private Config() {}

    public static String baseUrl() {
        return get("app.baseUrl", "http://localhost:8080");
    }

    public static String browser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "true"));
    }

    public static int implicitWaitSeconds() {
        return Integer.parseInt(get("implicit.wait.seconds", "5"));
    }

    public static int explicitWaitSeconds() {
        return Integer.parseInt(get("explicit.wait.seconds", "15"));
    }

    private static String get(String key, String fallback) {
        String sys = System.getProperty(key);
        String placeholder = "${" + key + "}";
        if (sys != null && !sys.isBlank() && !placeholder.equals(sys)) {
            return sys;
        }
        return PROPS.getProperty(key, fallback);
    }
}
