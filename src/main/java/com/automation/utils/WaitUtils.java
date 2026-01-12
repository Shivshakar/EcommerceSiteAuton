package com.automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Reusable wait helpers that centralize WebDriverWait creation.
 */
public final class WaitUtils {
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    private WaitUtils() {}

    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        return waitForVisibility(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public static WebElement waitForVisibility(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return waitForClickable(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public static WebElement waitForClickable(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void sleepMillis(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
