package com.automation.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Small browser utilities to handle flaky navigation caused by ad fragments or popups (e.g., google_vignette).
 */
public final class BrowserUtils {
    private BrowserUtils() {}

    /**
     * Safely click an anchor or button and ensure navigation to a page that contains {@code successLocator}.
     * Behavior:
     * - captures href before clicking as fallback
     * - clicks the element (with JS fallback)
     * - closes any new windows that were opened by the click (assumed ads), switches back
     * - waits for {@code successLocator} to be visible
     * - if navigation landed on an ad fragment (like '#google_vignette') or success locator not visible,
     *   navigates directly to {@code fallbackHref} if provided
     *
     * Returns true when successLocator becomes visible (navigation succeeded), false otherwise.
     */
    public static boolean safeClickAndNavigate(WebDriver driver, WebElement clickable, By successLocator, String fallbackHref, int timeoutSeconds) {
        String href = null;
        try { href = clickable.getAttribute("href"); } catch (Exception ignored) {}

        Set<String> before = new HashSet<>(driver.getWindowHandles());
        String original = driver.getWindowHandle();

        // attempt to click (native then JS fallback)
        try {
            WaitUtils.waitForClickable(driver, clickable);
            clickable.click();
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
            } catch (Exception ignored) {}
        }

        // if new window opened, close windows that look like ads and switch back
        try {
            Set<String> after = new HashSet<>(driver.getWindowHandles());
            if (after.size() > before.size()) {
                for (String h : after) {
                    if (!before.contains(h)) {
                        try {
                            driver.switchTo().window(h);
                            String cur = driver.getCurrentUrl();
                            // if ad-like, close it; otherwise keep focused on new window (but we prefer to close ad windows)
                            if (cur != null && cur.toLowerCase().contains("google_vignette")) {
                                driver.close();
                            } else {
                                // some sites open product details in a new tab; leave it open and switch to it
                                // but for safety, switch to it and proceed to wait for successLocator there
                            }
                        } catch (Exception ignored) {}
                    }
                }
                // switch back to original
                try { driver.switchTo().window(original); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // wait for successLocator
        try {
            WaitUtils.waitForVisibility(driver, successLocator, timeoutSeconds);
            return true;
        } catch (Exception ignored) {}

        // check current URL for known ad fragment and fallback to href
        try {
            String cur = driver.getCurrentUrl();
            if ((cur != null && cur.toLowerCase().contains("google_vignette")) || (fallbackHref != null && !fallbackHref.isEmpty())) {
                try {
                    URL base = new URL(driver.getCurrentUrl());
                    URL absolute = new URL(base, (fallbackHref != null && !fallbackHref.isEmpty()) ? fallbackHref : "");
                    driver.get(absolute.toString());
                } catch (Exception e) {
                    try { if (fallbackHref != null && !fallbackHref.isEmpty()) driver.get(fallbackHref); } catch (Exception ignored) {}
                }
                try {
                    WaitUtils.waitForVisibility(driver, successLocator, timeoutSeconds);
                    return true;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        return false;
    }
}

