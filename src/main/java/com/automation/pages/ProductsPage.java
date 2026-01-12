package com.automation.pages;

import com.automation.utils.BrowserUtils;
import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductsPage {
    private final WebDriver driver;

    // Navigation link
    private final By productsLink = By.xpath("//a[normalize-space()='Products' or contains(., 'Products')]");

    // Page header for All Products
    private final By allProductsHeader = By.xpath("//h2[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'all products')]");

    // Product list container and individual product blocks
    private final By productList = By.cssSelector(".features_items");
    private final By productBlocks = By.cssSelector(".features_items .col-sm-4");

    // --- Search locators ---
    private final By searchInput = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By searchedProductsHeader = By.xpath("//h2[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'searched products')]");

    // Product detail page stable locator (used to detect successful navigation)
    private final By productDetailName = By.xpath("//div[@class='product-information']//h2|//h2[@itemprop='name']");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickProductsLink() {
        WebElement link = WaitUtils.waitForClickable(driver, productsLink);
        String href = null;
        try { href = link.getAttribute("href"); } catch (Exception ignored) {}

        boolean ok = BrowserUtils.safeClickAndNavigate(driver, link, allProductsHeader, href != null ? href : (driver.getCurrentUrl() + "/products"), 10);
        if (!ok) {
            // final fallback: direct navigation to base/products
            try {
                String base = driver.getCurrentUrl();
                driver.get((href != null && !href.isEmpty()) ? href : base + "/products");
                WaitUtils.waitForVisibility(driver, allProductsHeader);
            } catch (Exception ignored) {}
        }

        ReportManager.step("Clicked Products link (safe)");
    }

    public boolean isAllProductsVisible() {
        try {
            WaitUtils.waitForVisibility(driver, allProductsHeader);
            ReportManager.step("All Products header visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("All Products header NOT visible");
            return false;
        }
    }

    public boolean isProductListVisible() {
        try {
            WaitUtils.waitForVisibility(driver, productList);
            ReportManager.step("Product list visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Product list NOT visible");
            return false;
        }
    }

    // --- Search helpers ---
    public void enterSearchQuery(String query) {
        WebElement input = WaitUtils.waitForVisibility(driver, searchInput);
        input.clear();
        input.sendKeys(query);
        ReportManager.step("Entered search query: " + query);
    }

    public void clickSearchButton() {
        WebElement btn = WaitUtils.waitForClickable(driver, searchButton);
        btn.click();
        ReportManager.step("Clicked search button");
    }

    public boolean isSearchedProductsVisible() {
        try {
            WaitUtils.waitForVisibility(driver, searchedProductsHeader);
            ReportManager.step("Searched Products header visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Searched Products header NOT visible");
            return false;
        }
    }

    public boolean areSearchResultsVisible() {
        return isProductListVisible();
    }

    // Click the 'View Product' for the product at given index (0-based)
    public void clickViewProduct(int index) {
        WaitUtils.waitForVisibility(driver, productList);
        List<WebElement> blocks = driver.findElements(productBlocks);
        if (blocks.size() > index) {
            WebElement block = blocks.get(index);

            // Capture any product_details href in this block first (used as a reliable fallback)
            String productHref = null;
            List<WebElement> anchorsForHref = block.findElements(By.cssSelector("a[href]"));
            for (WebElement a : anchorsForHref) {
                try {
                    String href = a.getAttribute("href");
                    if (href != null && href.contains("product_details")) {
                        productHref = href;
                        break;
                    }
                } catch (Exception ignored) {}
            }

            // Prefer link whose href contains 'product_details' to avoid anchors like '#google_vignette'
            WebElement targetAnchor = null;
            List<WebElement> anchors = block.findElements(By.tagName("a"));
            for (WebElement a : anchors) {
                try {
                    String href = a.getAttribute("href");
                    if (href != null && href.contains("product_details")) {
                        targetAnchor = a;
                        break;
                    }
                } catch (Exception ignored) {}
            }

            // fallback: find any anchor with link text 'View Product', prefer one with a meaningful href (not '#...')
            if (targetAnchor == null) {
                try {
                    List<WebElement> viewAnchors = block.findElements(By.xpath(".//a[contains(normalize-space(.),'View Product')]") );
                    for (WebElement va : viewAnchors) {
                        try {
                            String href = va.getAttribute("href");
                            if (href != null) {
                                String low = href.toLowerCase();
                                if (!low.startsWith("#") && !low.contains("google_vignette")) {
                                    targetAnchor = va;
                                    break;
                                }
                            } else {
                                // If href is null, prefer anchors that have onclick or data attributes - pick as last resort
                                if (targetAnchor == null) targetAnchor = va;
                            }
                        } catch (Exception ignored) {}
                    }
                    // if still null and we had any, pick the first
                    if (targetAnchor == null && !viewAnchors.isEmpty()) {
                        targetAnchor = viewAnchors.get(0);
                    }
                } catch (Exception ignored) {
                    // leave null
                }
            }

            if (targetAnchor == null && productHref == null) {
                throw new IllegalStateException("Could not find View Product anchor or product_details href in product block at index " + index);
            }

            // scroll into view (prefer targetAnchor if available, else use block)
            WebElement toScroll = (targetAnchor != null) ? targetAnchor : block;
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'auto', block:'center'});", toScroll);

            // Prepare window handles before clicking to detect new windows
            Set<String> beforeHandles = new HashSet<>(driver.getWindowHandles());
            String originalWindow = driver.getWindowHandle();

            // Try to click the anchor; use wait + JS fallback + direct navigation fallback
            boolean navigatedToDetail = false;
            if (targetAnchor != null) {
                try {
                    WaitUtils.waitForClickable(driver, targetAnchor);
                    targetAnchor.click();
                } catch (Exception e) {
                    ReportManager.step("Normal click failed on View Product anchor, trying JS click or direct navigation: " + e.getMessage());
                    try {
                        // JS click fallback
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetAnchor);
                    } catch (Exception jsEx) {
                        // leave; we'll rely on fallback navigation
                    }
                }

                // If clicking opened a new window, switch to it
                try {
                    Set<String> afterHandles = new HashSet<>(driver.getWindowHandles());
                    if (afterHandles.size() > beforeHandles.size()) {
                        // find the new handle
                        for (String h : afterHandles) {
                            if (!beforeHandles.contains(h)) {
                                driver.switchTo().window(h);
                                ReportManager.step("Switched to new window after clicking View Product: " + h);
                                break;
                            }
                        }
                    }
                } catch (Exception ignored) {}

                // Wait for product detail element to appear (strong indication of successful navigation)
                try {
                    WaitUtils.waitForVisibility(driver, productDetailName, 15);
                    navigatedToDetail = true;
                } catch (Exception ignored) {
                    // not visible yet
                }

                // If current URL is a known ad fragment like '#google_vignette', try to close the ad window and switch back
                try {
                    String current = driver.getCurrentUrl();
                    if (!navigatedToDetail && current != null && current.contains("google_vignette")) {
                        ReportManager.step("Detected google_vignette fragment after click; closing this window if it's a separate window and switching back");
                        try {
                            // If we're not on the original window, close the ad window and switch back
                            if (!driver.getWindowHandle().equals(originalWindow)) {
                                driver.close();
                                driver.switchTo().window(originalWindow);
                            } else {
                                // If it's the same window, navigate back to original URL
                                String originalUrl = new URL(driver.getCurrentUrl()).getProtocol() + "://" + new URL(driver.getCurrentUrl()).getHost();
                                driver.get(originalUrl);
                            }
                        } catch (Exception ignored) {}
                    }
                } catch (Exception ignored) {}
            }

            // If clicking didn't navigate to the detail page, use the captured productHref fallback (in the original window)
            if (!navigatedToDetail && productHref != null) {
                try {
                    // Ensure we're on the original window before direct navigation
                    try { driver.switchTo().window(originalWindow); } catch (Exception ignored) {}

                    ReportManager.step("Click did not navigate to product_details; navigating directly to captured href: " + productHref);
                    URL base = new URL(driver.getCurrentUrl());
                    URL absolute = new URL(base, productHref);
                    driver.get(absolute.toString());
                    // wait for product detail
                    try {
                        WaitUtils.waitForVisibility(driver, productDetailName, 10);
                        navigatedToDetail = true;
                    } catch (Exception ignored) {
                        navigatedToDetail = driver.getCurrentUrl().contains("product_details");
                    }
                } catch (Exception ex) {
                    try { driver.get(productHref); navigatedToDetail = driver.getCurrentUrl().contains("product_details"); } catch (Exception ignored) {}
                }
            }

            if (!navigatedToDetail) {
                ReportManager.step("After all fallbacks, navigation to product_details did not occur (currentUrl=" + driver.getCurrentUrl() + ")");
            }

            ReportManager.step("Clicked View Product for index: " + index + " (navigated=" + navigatedToDetail + ")");
        } else {
            throw new IllegalStateException("No product at index " + index + " (found " + blocks.size() + ")");
        }
    }
}
