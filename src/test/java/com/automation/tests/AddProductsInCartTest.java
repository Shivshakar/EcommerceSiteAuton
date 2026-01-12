package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.CartPage;
import com.automation.pages.ProductsPage;
import com.automation.pages.ProductDetailPage;
import com.automation.pages.HomePage;
import com.automation.utils.WaitUtils;
import com.automation.utils.BrowserUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class AddProductsInCartTest extends TestBase {

    @Test
    public void testAddProductsInCart() {
        HomePage home = new HomePage(driver);
        ProductsPage products = new ProductsPage(driver);
        CartPage cart = new CartPage(driver);

        // 1-2 Navigate to home
        home.goTo(baseUrl);

        // 3 Verify home visible by checking signup/login link
        Assert.assertTrue(home.isLoggedOut(), "Home page should be visible (Signup/Login present)");

        // 4 Click Products
        products.clickProductsLink();
        Assert.assertTrue(products.isAllProductsVisible(), "All Products should be visible after clicking Products");

        // 5 Hover over first product and click Add to cart
        // The site shows add-to-cart buttons inside product blocks; we'll find first two and click their 'Add to cart' buttons
        List<WebElement> blocks = driver.findElements(By.cssSelector(".features_items .col-sm-4"));
        Assert.assertTrue(blocks.size() >= 2, "Expect at least 2 products on the products page");

        // First product: click Add to cart
        WebElement first = blocks.get(0);
        WebElement add1 = first.findElement(By.xpath(".//a[contains(.,'Add to cart') or contains(.,'Add To Cart') or contains(@data-product-id,'add-to-cart')]") );
        BrowserUtils.safeClick(driver, add1);

        // 6 Click Continue Shopping button on modal
        // Try common selectors used by site for modal buttons
        try {
            WebElement cont = WaitUtils.waitForClickable(driver, By.xpath("//button[contains(.,'Continue Shopping') or contains(.,'Continue')]"), 5);
            BrowserUtils.safeClick(driver, cont);
        } catch (Exception e) {
            // ignore - some flows may not show modal
        }

        // 7 Second product: click Add to cart
        WebElement second = blocks.get(1);
        WebElement add2 = second.findElement(By.xpath(".//a[contains(.,'Add to cart') or contains(.,'Add To Cart') or contains(@data-product-id,'add-to-cart')]") );
        BrowserUtils.safeClick(driver, add2);

        // 8 Click View Cart
        try {
            WebElement view = WaitUtils.waitForClickable(driver, CartPage.viewCartButton, 5);
            BrowserUtils.safeClick(driver, view);
        } catch (Exception e) {
            // fallback: navigate to /view_cart
            driver.get(baseUrl + "/view_cart");
        }

        // 9 Verify both products added
        int count = cart.getNumberOfItems();
        Assert.assertTrue(count >= 2, "Expected at least 2 items in cart (actual=" + count + ")");

        // 10 Verify their prices/quantity/subtotal are present (best-effort assertions)
        String p1Name = cart.getProductNameAt(0);
        String p2Name = cart.getProductNameAt(1);
        Assert.assertFalse(p1Name.isEmpty(), "First product name should be present in cart");
        Assert.assertFalse(p2Name.isEmpty(), "Second product name should be present in cart");

        String p1Price = cart.getProductPriceAt(0);
        String p2Price = cart.getProductPriceAt(1);
        Assert.assertFalse(p1Price.isEmpty(), "First product price should be present");
        Assert.assertFalse(p2Price.isEmpty(), "Second product price should be present");

        String p1Sub = cart.getProductSubtotalAt(0);
        Assert.assertFalse(p1Sub.isEmpty(), "First product subtotal should be present");
    }
}
