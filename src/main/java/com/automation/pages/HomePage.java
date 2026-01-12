package com.automation.pages;

import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage {
    private final WebDriver driver;

    private final By signupLoginLink = By.xpath("//a[contains(text(),'Signup / Login') or contains(text(),'Sign Up / Login')]");

    // Login-related locators (useful after navigating to the login page)
    private final By loginEmailInput = By.cssSelector("input[data-qa='login-email']");
    private final By loginPasswordInput = By.cssSelector("input[data-qa='login-password']");
    private final By loginButton = By.xpath("//button[contains(text(),'Login') or contains(text(),'Log In')]");
    private final By loggedInAs = By.xpath("//a[contains(text(),'Logged in as')]");
    // Logout locator
    private final By logoutLink = By.xpath("//a[contains(text(),'Logout') or text()='Logout']");

    // Cart locator - moved to page object so tests don't use inline locators
    private final By cartLink = By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cart') or contains(., 'Cart')]");

    // --- Subscription/footer locators moved here from tests ---
    // The site sometimes uses a misspelled id for the subscription input; include common fallbacks in the selector.
    private final By subscriptionHeading = By.xpath("//h2[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'subscription')]");
    private final By subscriptionInput = By.cssSelector("#susbscribe_email, input[placeholder*='Your email'], input[placeholder*='Enter your email'], input[type='email']");
    private final By subscriptionSubmit = By.cssSelector("#subscribe_submit, button#subscribe, button[type='submit'], button[class*='subscribe'], .arrow-btn");
    private final By subscriptionSuccess = By.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully subscribed') or contains(., 'You have been successfully subscribed')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void goTo(String baseUrl) {
        driver.get(baseUrl);
        ReportManager.step("Navigate to: " + baseUrl);
        WaitUtils.waitForVisibility(driver, signupLoginLink);
    }

    public void clickSignupLogin() {
        WebElement link = WaitUtils.waitForClickable(driver, signupLoginLink);
        link.click();
        ReportManager.step("Clicked Signup / Login link");
    }

    // --- Login helpers (operate after clickSignupLogin navigates to the login form) ---

    public void enterLoginEmail(String email) {
        WebElement el = WaitUtils.waitForVisibility(driver, loginEmailInput);
        el.clear();
        el.sendKeys(email);
        ReportManager.step("Entered login email: " + email);
    }

    public void enterLoginPassword(String pwd) {
        WebElement el = WaitUtils.waitForVisibility(driver, loginPasswordInput);
        el.clear();
        el.sendKeys(pwd);
        ReportManager.step("Entered login password");
    }

    public void clickLoginButton() {
        WebElement btn = WaitUtils.waitForClickable(driver, loginButton);
        btn.click();
        ReportManager.step("Clicked Login button");
    }

    public void loginAs(String email, String pwd) {
        enterLoginEmail(email);
        enterLoginPassword(pwd);
        clickLoginButton();
    }

    public boolean isLoggedIn() {
        try {
            WaitUtils.waitForVisibility(driver, loggedInAs);
            ReportManager.step("User appears logged in");
            return true;
        } catch (Exception e) {
            ReportManager.step("User not logged in");
            return false;
        }
    }

    // Clicks the logout link (should be visible when user is logged in)
    public void clickLogout() {
        WebElement link = WaitUtils.waitForClickable(driver, logoutLink);
        link.click();
        ReportManager.step("Clicked Logout link");
    }

    // Returns true when the user is logged out (we detect by the presence of the Signup / Login link)
    public boolean isLoggedOut() {
        try {
            WaitUtils.waitForVisibility(driver, signupLoginLink);
            ReportManager.step("User appears logged out");
            return true;
        } catch (Exception e) {
            ReportManager.step("User not logged out");
            return false;
        }
    }

    // --- Cart helper (best-effort) ---
    /**
     * Attempts to click the cart link/button if it exists. This is a best-effort helper; it will
     * catch exceptions and log via ReportManager instead of throwing so tests don't fail when a
     * cart button isn't present.
     */
    public void clickCart() {
        try {
            WebElement el = WaitUtils.waitForClickable(driver, cartLink, 3);
            el.click();
            ReportManager.step("Clicked Cart link");
        } catch (Exception e) {
            // Don't fail the test — cart is optional in some flows/sites. Log and continue.
            ReportManager.step("Cart link not present or could not be clicked: " + e.getMessage());
        }
    }

    // --- Subscription/footer helpers ---

    // Scrolls to page footer so subscription input is visible.
    public void scrollToFooter() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        } catch (Exception ignored) {
            // Best-effort scroll; ignore if JS execution not available for some driver implementations.
        }
        ReportManager.step("Scrolled to footer");
    }

    public WebElement getSubscriptionHeading() {
        return WaitUtils.waitForVisibility(driver, subscriptionHeading);
    }

    public void enterSubscriptionEmail(String email) {
        WebElement input = WaitUtils.waitForVisibility(driver, subscriptionInput);
        input.clear();
        input.sendKeys(email);
        ReportManager.step("Entered subscription email: " + email);
    }

    public void clickSubscribe() {
        WebElement btn = WaitUtils.waitForClickable(driver, subscriptionSubmit);
        btn.click();
        ReportManager.step("Clicked subscription submit button");
    }

    public WebElement getSubscriptionSuccessMessage() {
        return WaitUtils.waitForVisibility(driver, subscriptionSuccess, 10);
    }

    // convenience helper that performs the whole subscription flow and returns the success message text
    public String subscribe(String email) {
        scrollToFooter();
        enterSubscriptionEmail(email);
        clickSubscribe();
        WebElement msg = getSubscriptionSuccessMessage();
        return msg == null ? null : msg.getText();
    }
}
