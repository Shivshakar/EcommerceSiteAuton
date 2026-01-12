package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage {
    private final WebDriver driver;

    // cart table rows
    private final By cartTableRows = By.cssSelector("table.table tbody tr");
    private final By productNameCell = By.cssSelector("td.cart_description h4 a, td.cart_description p a");
    private final By productPriceCell = By.cssSelector("td.cart_price p, td.cart_price");
    private final By productSubtotalCell = By.cssSelector("td.cart_total p, td.cart_total");

    // View Cart friendly locator (used on other pages)
    public static final By viewCartButton = By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view cart') or contains(., 'View Cart')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public int getNumberOfItems() {
        try {
            // fetch all rows directly (previous code waited for a single row WebElement and then used findElements('.') which returned only that one)
            List<WebElement> rows = driver.findElements(cartTableRows);

            // if no rows found yet, wait for the tbody (or first row) to be visible and retry
            if (rows.isEmpty()) {
                try {
                    WaitUtils.waitForVisibility(driver, By.cssSelector("table.table tbody"), 5);
                } catch (Exception ignored) {}
                rows = driver.findElements(cartTableRows);
            }

            // Sum quantities from each row when possible (site may group duplicate products into one row)
            int total = 0;
            for (WebElement row : rows) {
                try {
                    // prefer input[type='text'] value
                    WebElement input = null;
                    try {
                        input = row.findElement(By.cssSelector("td.cart_quantity input[type='text']"));
                    } catch (Exception ignored) {}

                    if (input != null) {
                        String v = input.getAttribute("value");
                        if (v == null || v.isEmpty()) v = input.getText();
                        try {
                            int q = Integer.parseInt(v.replaceAll("[^0-9]", ""));
                            total += Math.max(1, q);
                            continue;
                        } catch (Exception ignored) {}
                    }

                    // fallback: try to read any textual quantity in the quantity cell
                    try {
                        WebElement qtyCell = row.findElement(By.cssSelector("td.cart_quantity"));
                        String text = qtyCell.getText();
                        int q = Integer.parseInt(text.replaceAll("[^0-9]", ""));
                        total += Math.max(1, q);
                        continue;
                    } catch (Exception ignored) {}

                    // final fallback: count the row as 1 item
                    total += 1;
                } catch (Exception e) {
                    total += 1;
                }
            }

            return total;
        } catch (Exception e) {
            // fallback: attempt to find elements
            try {
                List<WebElement> rows = driver.findElements(cartTableRows);
                return rows.size();
            } catch (Exception ex) { return 0; }
        }
    }

    public String getProductNameAt(int index) {
        List<WebElement> rows = driver.findElements(cartTableRows);
        if (rows.size() > index) {
            try {
                return rows.get(index).findElement(productNameCell).getText();
            } catch (Exception e) { return ""; }
        }
        return "";
    }

    public String getProductPriceAt(int index) {
        List<WebElement> rows = driver.findElements(cartTableRows);
        if (rows.size() > index) {
            try {
                return rows.get(index).findElement(productPriceCell).getText();
            } catch (Exception e) { return ""; }
        }
        return "";
    }

    public String getProductSubtotalAt(int index) {
        List<WebElement> rows = driver.findElements(cartTableRows);
        if (rows.size() > index) {
            try {
                return rows.get(index).findElement(productSubtotalCell).getText();
            } catch (Exception e) { return ""; }
        }
        return "";
    }
}
