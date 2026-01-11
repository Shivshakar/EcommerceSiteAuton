package com.automation.pages;

import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.By;
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
}
