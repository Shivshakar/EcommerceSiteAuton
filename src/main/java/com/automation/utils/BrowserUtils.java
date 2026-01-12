package com.automation.utils;

import org.openqa.selenium.*;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Small browser utilities to handle flaky navigation caused by ad fragments or popups (e.g., google_vignette).
 */
public final class BrowserUtils {
    private BrowserUtils() {}

    /**
     * Attempts to hide/remove known ad iframes and overlays that commonly intercept clicks.
     * This uses a conservative JS approach: it hides elements matching a small whitelist of
     * selectors (aswift iframes, doubleclick/google ad iframes, common overlay/backdrop classes)
     * and tries to click common close buttons if present.
     */
    private static void dismissKnownOverlays(WebDriver driver) {
        try {
            String script = "(function(){"
                    + "var selectors = ["
                    + "'iframe[id^=\\\'aswift\\\']',"
                    + "'iframe[src*=\\\"doubleclick\\\"]',"
                    + "'iframe[src*=\\\"google\\\"]',"
                    + "'div[id*=\\\"google_vignette\\\"]',"
                    + "'div[class*=\\\"overlay\\\"]',"
                    + "'.modal-backdrop'"
                    + "];"
                    + "selectors.forEach(function(s){ try{ var nodes=document.querySelectorAll(s); nodes.forEach(function(n){ try{ n.style.pointerEvents='none'; n.style.visibility='hidden'; n.style.display='none'; if(n.parentNode) n.parentNode.removeChild(n);}catch(e){} }); }catch(e){} });"
                    + "var closeSelectors = ['button.close','button[aria-label=\\\'Close\\\']','button[aria-label=\\\'close\\\']','.close','.dismiss'];"
                    + "closeSelectors.forEach(function(cs){ try{ document.querySelectorAll(cs).forEach(function(b){ try{ b.click(); }catch(e){} }); }catch(e){} });"
                    + "return true; })();";
            ((JavascriptExecutor) driver).executeScript(script);
        } catch (Exception ignored) {
            // best-effort only
        }
    }

    /**
     * Safely click an anchor or button and ensure navigation to a page that contains {@code successLocator}.
     * Behavior:
     * - captures href before clicking as fallback
     * - dismisses known overlays (ads) before clicking
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

        // Dismiss overlays before attempting click
        dismissKnownOverlays(driver);

        // attempt to click (native then JS fallback). If click is intercepted, try dismissing overlays and retry once.
        boolean clicked = false;
        try {
            WaitUtils.waitForClickable(driver, clickable);
            clickable.click();
            clicked = true;
        } catch (org.openqa.selenium.ElementClickInterceptedException intercepted) {
            // overlay blocking click - try to dismiss and retry
            dismissKnownOverlays(driver);
            try {
                WaitUtils.waitForClickable(driver, clickable);
                clickable.click();
                clicked = true;
            } catch (Exception e) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
                    clicked = true;
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
                clicked = true;
            } catch (Exception ignored) {}
        }

        // if we never managed to click, give up early
        if (!clicked) {
            // still attempt fallback navigation below
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
                                // keep it open (may be product details); we'll switch back to original below if needed
                            }
                        } catch (Exception ignored) {}
                    }
                }
                // ensure focus on original window
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

    // New safeClick helpers: try native click, on intercept dismiss overlays and retry, fallback to JS click
    public static void safeClick(WebDriver driver, WebElement element) {
        safeClick(driver, element, 3, 500);
    }

    public static void safeClick(WebDriver driver, By locator) {
        safeClick(driver, locator, 3, 500);
    }

    public static void safeClick(WebDriver driver, WebElement element, int attempts, long waitBetweenMillis) {
        if (attempts < 1) attempts = 1;
        int tries = 0;
        while (tries < attempts) {
            tries++;
            try {
                // ensure element is present & clickable
                WaitUtils.waitForClickable(driver, element);
                element.click();
                return;
            } catch (org.openqa.selenium.ElementClickInterceptedException intercepted) {
                // overlay blocking click - try to dismiss and retry
                dismissKnownOverlays(driver);
                try {
                    Thread.sleep(waitBetweenMillis);
                } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            } catch (org.openqa.selenium.WebDriverException we) {
                // some other issue (stale, not interactable etc.) - try dismissing overlays and retry
                dismissKnownOverlays(driver);
                try {
                    Thread.sleep(waitBetweenMillis);
                } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }

        // final attempt using JS click as a last resort
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception ignored) {
            // give up - best effort only
        }
    }

    public static void safeClick(WebDriver driver, By locator, int attempts, long waitBetweenMillis) {
        if (attempts < 1) attempts = 1;
        int tries = 0;
        while (tries < attempts) {
            tries++;
            try {
                WebElement el = WaitUtils.waitForClickable(driver, locator);
                el.click();
                return;
            } catch (WebDriverException we) {
                dismissKnownOverlays(driver);
                try {
                    Thread.sleep(waitBetweenMillis);
                } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }

        // final JS click fallback
        try {
            WebElement el = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (Exception ignored) {
            // best-effort
        }
    }
}