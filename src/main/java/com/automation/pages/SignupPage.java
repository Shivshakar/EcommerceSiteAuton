package com.automation.pages;

import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class SignupPage {
    private final WebDriver driver;

    // Locators for the 'New User Signup' form
    private final By nameInput = By.xpath("//input[@name='name' or @placeholder='Name']");
    private final By emailInput = By.xpath("//input[@data-qa='signup-email']");
    private final By signupButton = By.xpath("//button[contains(text(),'Signup') or contains(text(),'Sign up')]");

    // Locators for account information form (after clicking Signup)
    private final By titleMr = By.id("id_gender1");
    private final By titleMrs = By.id("id_gender2");
    private final By password = By.id("password");
    private final By days = By.id("days");
    private final By months = By.id("months");
    private final By years = By.id("years");
    private final By newsletterCheckbox = By.id("newsletter");
    private final By offersCheckbox = By.id("optin");

    private final By firstName = By.id("first_name");
    private final By lastName = By.id("last_name");
    private final By company = By.id("company");
    private final By address1 = By.id("address1");
    private final By address2 = By.id("address2");
    private final By country = By.id("country");
    private final By state = By.id("state");
    private final By city = By.id("city");
    private final By zipcode = By.id("zipcode");
    private final By mobileNumber = By.id("mobile_number");

    private final By createAccountButton = By.xpath("//button[contains(text(),'Create Account') or contains(text(),'Create Account')]");

    private final By accountCreatedMsg = By.cssSelector("h2[data-qa='account-created']");

    public SignupPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterNameAndEmail(String name, String email) {

        WebElement nameEl = WaitUtils.waitForVisibility(driver, nameInput);
        nameEl.clear();
        nameEl.sendKeys(name);
        ReportManager.step("Entered name: " + name);

        WebElement emailEl = WaitUtils.waitForVisibility(driver, emailInput);
        emailEl.clear();
        emailEl.sendKeys(email);
        ReportManager.step("Entered email: " + email);
    }

    public void clickSignupButton() {
        WebElement btn = WaitUtils.waitForClickable(driver, signupButton);
        btn.click();
        ReportManager.step("Clicked Signup button");
    }

    public void fillAccountInformation(boolean selectTitleMr, String pwd,
                                       String day, String month, String year,
                                       boolean newsletter, boolean offers,
                                       String fName, String lName, String comp,
                                       String addr1, String addr2, String countryVal,
                                       String stateVal, String cityVal, String zip, String mobile) {
        // Wait for password field to appear
        WaitUtils.waitForVisibility(driver, password);
        ReportManager.step("Filling account information");

        if (selectTitleMr) {
            driver.findElement(titleMr).click();
            ReportManager.step("Selected title: Mr");
        } else {
            driver.findElement(titleMrs).click();
            ReportManager.step("Selected title: Mrs");
        }
        driver.findElement(password).sendKeys(pwd);
        ReportManager.step("Entered password");
        new Select(driver.findElement(days)).selectByValue(day);
        new Select(driver.findElement(months)).selectByValue(month);
        new Select(driver.findElement(years)).selectByValue(year);
        ReportManager.step("Selected DOB: " + day + "/" + month + "/" + year);

        if (newsletter) {
            if (!driver.findElement(newsletterCheckbox).isSelected()) {
                driver.findElement(newsletterCheckbox).click();
            }
            ReportManager.step("Newsletter checkbox selected");
        }
        if (offers) {
            if (!driver.findElement(offersCheckbox).isSelected()) {
                driver.findElement(offersCheckbox).click();
            }
            ReportManager.step("Offers checkbox selected");
        }

        // Fill address info
        driver.findElement(firstName).sendKeys(fName);
        driver.findElement(lastName).sendKeys(lName);
        driver.findElement(company).sendKeys(comp);
        driver.findElement(address1).sendKeys(addr1);
        driver.findElement(address2).sendKeys(addr2);
        new Select(driver.findElement(country)).selectByVisibleText(countryVal);
        driver.findElement(state).sendKeys(stateVal);
        driver.findElement(city).sendKeys(cityVal);
        driver.findElement(zipcode).sendKeys(zip);
        driver.findElement(mobileNumber).sendKeys(mobile);
        ReportManager.step("Filled address and contact info");
    }

    public void clickCreateAccount() {
        WebElement btn = WaitUtils.waitForClickable(driver, createAccountButton);
        btn.click();
        ReportManager.step("Clicked Create Account button");
    }

    public boolean isAccountCreated() {
        try {
            WaitUtils.waitForVisibility(driver, accountCreatedMsg);
            ReportManager.step("Account created message visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Account created message NOT visible");
            return false;
        }
    }
}
