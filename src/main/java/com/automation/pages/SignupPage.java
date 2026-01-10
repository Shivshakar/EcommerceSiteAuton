package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SignupPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterNameAndEmail(String name, String email) {
        WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        nameEl.clear();
        nameEl.sendKeys(name);

        // Wait a short time for page to react to name input (some pages reveal the email field only after typing)
        try {
            WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));

            // Diagnostics: print state if email found but not interactable
            if (!emailEl.isDisplayed() || !emailEl.isEnabled()) {
                System.out.println("Email field found but not interactable: displayed=" + emailEl.isDisplayed() + ", enabled=" + emailEl.isEnabled());
            }

            emailEl.clear();
            emailEl.sendKeys(email);
        } catch (Exception e) {
            // Try a fallback locator (common email input patterns)
            System.out.println("Primary email locator failed: " + e.getMessage() + "; trying fallback locators...");
            try {
                By altEmail = By.xpath("//input[@type='email' and (contains(@placeholder,'Email') or contains(translate(@name,'EMAIL','email'),'email'))]");
                WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(altEmail));
                emailEl.clear();
                emailEl.sendKeys(email);
                System.out.println("Used fallback email locator");
            } catch (Exception ex) {
                System.out.println("Fallback email locator also failed: " + ex.getMessage());
                // As last resort try to find any visible input other than name and use it
                try {
                    WebElement anyInput = driver.findElement(By.xpath("//input[not(@type='hidden') and normalize-space(@value)='']"));
                    if (!anyInput.equals(nameEl)) {
                        anyInput.clear();
                        anyInput.sendKeys(email);
                        System.out.println("Used generic input fallback to enter email");
                    }
                } catch (Exception ignored) {
                    // ignored
                }
                throw new RuntimeException("Failed to locate or fill email input in enterNameAndEmail", ex);
            }
        }
    }

    public void clickSignupButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(signupButton));
        btn.click();
    }

    public void fillAccountInformation(boolean selectTitleMr, String pwd,
                                       String day, String month, String year,
                                       boolean newsletter, boolean offers,
                                       String fName, String lName, String comp,
                                       String addr1, String addr2, String countryVal,
                                       String stateVal, String cityVal, String zip, String mobile) {
        // Wait for password field to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(password));

        if (selectTitleMr) {
            driver.findElement(titleMr).click();
        } else {
            driver.findElement(titleMrs).click();
        }
        driver.findElement(password).sendKeys(pwd);
        new Select(driver.findElement(days)).selectByValue(day);
        new Select(driver.findElement(months)).selectByValue(month);
        new Select(driver.findElement(years)).selectByValue(year);

        if (newsletter) {
            if (!driver.findElement(newsletterCheckbox).isSelected()) {
                driver.findElement(newsletterCheckbox).click();
            }
        }
        if (offers) {
            if (!driver.findElement(offersCheckbox).isSelected()) {
                driver.findElement(offersCheckbox).click();
            }
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
    }

    public void clickCreateAccount() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createAccountButton));
        btn.click();
    }

    public boolean isAccountCreated() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(accountCreatedMsg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
