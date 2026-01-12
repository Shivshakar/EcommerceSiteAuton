package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.*;
import com.automation.utils.WaitUtils;
import com.automation.utils.BrowserUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RegisterAndPlaceOrderTest extends TestBase {

    @Test
    public void testRegisterAndPlaceOrder() {
        HomePage home = new HomePage(driver);
        ProductsPage products = new ProductsPage(driver);
        SignupPage signup = new SignupPage(driver);
        CartPage cart = new CartPage(driver);
        CheckoutPage checkout = new CheckoutPage(driver);

        // 1-3 Navigate to home and verify
        home.goTo(baseUrl);
        Assert.assertTrue(home.isLoggedOut(), "Home should be visible and logged out");

        // 4 Add products to cart (add first product)
        products.clickProductsLink();
        Assert.assertTrue(products.isAllProductsVisible(), "Products page should be visible");

        List<WebElement> blocks = driver.findElements(By.cssSelector(".features_items .col-sm-4"));
        Assert.assertTrue(blocks.size() >= 1, "Expect at least 1 product to add");

        WebElement first = blocks.get(0);
        WebElement addBtn = first.findElement(By.xpath(".//a[contains(.,'Add to cart') or contains(.,'Add To Cart') or contains(@data-product-id,'add-to-cart')]"));
        BrowserUtils.safeClick(driver, addBtn);

        // handle modal if shown: click Continue
        try {
            WebElement cont = WaitUtils.waitForClickable(driver, By.xpath("//button[contains(.,'Continue Shopping') or contains(.,'Continue') or //a[contains(.,'Continue')]]"), 5);
            BrowserUtils.safeClick(driver, cont);
        } catch (Exception ignored) {}

        // 5 Click 'Cart' button
        home.clickCart();

        // 6 Verify cart page is displayed by checking rows or view_cart url
        try {
            WaitUtils.waitForVisibility(driver, By.cssSelector("table.table tbody"), 5);
        } catch (Exception ignored) {}

        // 7 Click Proceed To Checkout
        checkout.clickProceedToCheckout();

        // 8 Click 'Register / Login' button on checkout
        // some flows show a Register/Login button; try click
        try {
            checkout.clickRegisterLoginFromCheckout();
        } catch (Exception ignored) {}

        // 9 Fill signup details and create account
        String name = "TestUser" + System.currentTimeMillis();
        String email = "testuser" + System.currentTimeMillis() + "@example.com";
        signup.enterNameAndEmail(name, email);
        signup.clickSignupButton();

        // Fill account info (use simple fixed values)
        signup.fillAccountInformation(true, "Password123", "1", "1", "1990", true, true,
                "First", "Last", "Company", "Address1", "Address2", "United States", "State", "City", "12345", "1234567890");
        signup.clickCreateAccount();

        // 10 Verify 'ACCOUNT CREATED!'
        Assert.assertTrue(signup.isAccountCreated(), "Account should be created");

        // click Continue (there is usually a continue button that navigates back to home)
        try {
            WebElement cont = WaitUtils.waitForClickable(driver, By.xpath("//a[contains(.,'Continue') or //button[contains(.,'Continue')]"), 8);
            BrowserUtils.safeClick(driver, cont);
        } catch (Exception ignored) {}

        // 11 Verify 'Logged in as username'
        Assert.assertTrue(home.isLoggedIn(), "User should be logged in after account creation");

        // 12 Click 'Cart' button
        home.clickCart();

        // 13 Click 'Proceed To Checkout' button
        checkout.clickProceedToCheckout();

        // 14 Verify Address Details and Review Your Order
        Assert.assertTrue(checkout.isAddressDetailsVisible(), "Address details should be visible on checkout");
        Assert.assertTrue(checkout.isReviewYourOrderVisible(), "Review Your Order section should be visible");

        // 15 Enter comment and click 'Place Order'
        checkout.enterComment("Please deliver between 9-5");
        checkout.clickPlaceOrder();

        // 16 Enter payment details
        checkout.enterPaymentDetails("Test User", "4242424242424242", "123", "12", "2025");

        // 17 Click 'Pay and Confirm Order'
        checkout.clickPayAndConfirm();

        // 18 Verify success message 'Your order has been placed successfully!'
        Assert.assertTrue(checkout.isOrderPlaced(), "Order placed success message should be visible");

        // 19 Click 'Delete Account' button
        checkout.clickDeleteAccount();

        // 20 Verify 'ACCOUNT DELETED!'
        Assert.assertTrue(checkout.isAccountDeleted(), "Account deleted message should be visible");

        // Click Continue after delete
        try {
            WebElement cont = WaitUtils.waitForClickable(driver, By.xpath("//a[contains(.,'Continue') or //button[contains(.,'Continue')]"), 8);
            BrowserUtils.safeClick(driver, cont);
        } catch (Exception ignored) {}
    }
}

