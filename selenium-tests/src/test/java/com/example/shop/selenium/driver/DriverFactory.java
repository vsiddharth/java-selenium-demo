package com.example.shop.selenium.driver;

import com.example.shop.selenium.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe driver provider so TestNG can run classes in parallel later if we want.
 * Uses Selenium 4's built-in driver manager — no chromedriver/geckodriver download required.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver get() {
        if (TL.get() == null) {
            TL.set(create());
        }
        return TL.get();
    }

    public static void quit() {
        WebDriver driver = TL.get();
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            TL.remove();
        }
    }

    private static WebDriver create() {
        String browser = Config.browser();
        boolean headless = Config.headless();

        WebDriver driver = switch (browser) {
            case "firefox" -> {
                FirefoxOptions opts = new FirefoxOptions();
                if (headless) opts.addArguments("-headless");
                opts.addArguments("--width=1280", "--height=900");
                yield new FirefoxDriver(opts);
            }
            default -> {
                ChromeOptions opts = new ChromeOptions();
                if (headless) opts.addArguments("--headless=new");
                opts.addArguments(
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--disable-gpu",
                        "--window-size=1280,900");
                yield new ChromeDriver(opts);
            }
        };

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Config.implicitWaitSeconds()));
        return driver;
    }
}
