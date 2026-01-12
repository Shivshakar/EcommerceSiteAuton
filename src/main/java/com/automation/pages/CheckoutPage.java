package com.automation.pages;

import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckoutPage {
    private final WebDriver driver;

    private final By proceedToCheckoutButton = By.xpath("//a[contains(.,'Proceed To Checkout') or //button[contains(.,'Proceed To Checkout')] | //button[contains(.,'Proceed to Checkout')]");
    private final By registerLoginButton = By.xpath("//a[contains(.,'Register / Login') or contains(.,'Register / Login') or //button[contains(.,'Register / Login')]]");

    private final By addressDetailsSection = By.xpath("//*[contains(.,'Address Details') or contains(.,'Address Details & Payment') or //h2[contains(.,'Address Details')]]");
    private final By reviewYourOrderSection = By.xpath("//*[contains(.,'Review Your Order') or contains(.,'Review Your Order')] ");

    private final By commentTextarea = By.name("message");
    private final By placeOrderButton = By.xpath("//a[contains(.,'Place Order') or //button[contains(.,'Place Order')]]");

    // Payment form fields
    private final By nameOnCard = By.name("name_on_card");
    private final By cardNumber = By.name("card_number");
    private final By cvc = By.name("cvc");
    private final By expiryMonth = By.name("expiry_month");
    private final By expiryYear = By.name("expiry_year");
    private final By payAndConfirmButton = By.xpath("//button[contains(.,'Pay and Confirm Order') or contains(.,'Pay & Confirm Order') or contains(.,'Pay and Confirm')]");

    private final By orderPlacedSuccess = By.xpath("//*[contains(.,'Your order has been placed successfully') or contains(.,'Your order has been placed successfully!') or contains(.,'Order Placed!')]");

    private final By deleteAccountButton = By.xpath("//a[contains(.,'Delete Account') or contains(.,'Delete account')]");
    private final By accountDeletedMsg = By.xpath("//*[contains(.,'ACCOUNT DELETED') or contains(.,'ACCOUNT DELETED!') or contains(.,'Account Deleted')]");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickProceedToCheckout() {
        try {
            WebElement btn = WaitUtils.waitForClickable(driver, proceedToCheckoutButton, 8);
            btn.click();
            ReportManager.step("Clicked Proceed To Checkout");
        } catch (Exception e) {
            // Try fallback by clicking any button/link text containing 'checkout'
            try {
                WebElement fallback = WaitUtils.waitForClickable(driver, By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'checkout')]"), 5);
                fallback.click();
                ReportManager.step("Clicked fallback checkout link");
            } catch (Exception ex) {
                ReportManager.step("Could not click Proceed To Checkout: " + ex.getMessage());
                throw new RuntimeException("Proceed To Checkout not found/clickable", ex);
            }
        }
    }

    public void clickRegisterLoginFromCheckout() {
        WebElement btn = WaitUtils.waitForClickable(driver, registerLoginButton, 8);
        btn.click();
        ReportManager.step("Clicked Register / Login from Checkout");
    }

    public boolean isAddressDetailsVisible() {
        try {
            WaitUtils.waitForVisibility(driver, addressDetailsSection, 8);
            ReportManager.step("Address Details visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Address Details NOT visible");
            return false;
        }
    }

    public boolean isReviewYourOrderVisible() {
        try {
            WaitUtils.waitForVisibility(driver, reviewYourOrderSection, 8);
            ReportManager.step("Review Your Order visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Review Your Order NOT visible");
            return false;
        }
    }

    public void enterComment(String comment) {
        try {
            WebElement el = WaitUtils.waitForVisibility(driver, commentTextarea, 5);
            el.clear();
            el.sendKeys(comment);
            ReportManager.step("Entered comment: " + comment);
        } catch (Exception e) {
            ReportManager.step("Comment textarea not found: " + e.getMessage());
        }
    }

    public void clickPlaceOrder() {
        WebElement btn = WaitUtils.waitForClickable(driver, placeOrderButton, 8);
        btn.click();
        ReportManager.step("Clicked Place Order");
    }

    public void enterPaymentDetails(String name, String cardNum, String cvcVal, String month, String year) {
        WaitUtils.waitForVisibility(driver, nameOnCard, 5).sendKeys(name);
        WaitUtils.waitForVisibility(driver, cardNumber, 5).sendKeys(cardNum);
        WaitUtils.waitForVisibility(driver, cvc, 5).sendKeys(cvcVal);
        WaitUtils.waitForVisibility(driver, expiryMonth, 5).sendKeys(month);
        WaitUtils.waitForVisibility(driver, expiryYear, 5).sendKeys(year);
        ReportManager.step("Entered payment details");
    }

    public void clickPayAndConfirm() {
        WebElement btn = WaitUtils.waitForClickable(driver, payAndConfirmButton, 8);
        btn.click();
        ReportManager.step("Clicked Pay and Confirm Order");
    }

    public boolean isOrderPlaced() {
        try {
            WaitUtils.waitForVisibility(driver, orderPlacedSuccess, 10);
            ReportManager.step("Order placed success message visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Order placed message NOT visible: " + e.getMessage());
            return false;
        }
    }

    public void clickDeleteAccount() {
        WebElement btn = WaitUtils.waitForClickable(driver, deleteAccountButton, 8);
        btn.click();
        ReportManager.step("Clicked Delete Account");
    }

    public boolean isAccountDeleted() {
        try {
            WaitUtils.waitForVisibility(driver, accountDeletedMsg, 8);
            ReportManager.step("Account deleted message visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Account deleted message NOT visible");
            return false;
        }
    }
}

