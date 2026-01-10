package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage {
    private final WebDriver driver;

    private final By signupLoginLink = By.xpath("//a[contains(text(),'Signup / Login') or contains(text(),'Sign Up / Login')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void goTo(String baseUrl) {
        driver.get(baseUrl);
        WaitUtils.waitForVisibility(driver, signupLoginLink);
    }

    public void clickSignupLogin() {
        WebElement link = WaitUtils.waitForClickable(driver, signupLoginLink);
        link.click();
    }
}
