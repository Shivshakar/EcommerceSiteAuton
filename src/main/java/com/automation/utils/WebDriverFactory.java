package com.automation.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WebDriverFactory {

    public static WebDriver createDriver() {

        WebDriverManager.chromedriver().clearDriverCache().setup();

        ChromeOptions options = new ChromeOptions();

// ---- Preferences ----
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.autofill.profile_enabled", false);

// 🔑 Disable ads & popups at browser level
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.popups", 2);
        prefs.put("profile.default_content_setting_values.ads", 2);

        options.setExperimentalOption("prefs", prefs);

// ---- Arguments ----
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--window-size=1920,1080");

// Optional but useful for CI
        options.addArguments("--remote-allow-origins=*");

        // Optional: enable headless for CI
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        try {
            Path tmpLog = Files.createTempFile("chromedriver-", ".log");

            ChromeDriverService service = new ChromeDriverService.Builder()
                    .withLogFile(tmpLog.toFile())
                    .build();

            return new ChromeDriver(service, options);
        } catch (Exception e) {
            // Fallback if custom service fails
            return new ChromeDriver(options);
        }
    }
}