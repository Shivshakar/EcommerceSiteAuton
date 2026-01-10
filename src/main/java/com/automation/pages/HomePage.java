package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    private final WebDriver driver;

    private final By signupLoginLink = By.xpath("//a[contains(text(),'Signup / Login') or contains(text(),'Sign Up / Login')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void goTo(String baseUrl) {
        driver.get(baseUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(signupLoginLink));
    }

    public void clickSignupLogin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(signupLoginLink));
        link.click();
    }
}
