package com.automation.pages;

import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Path;

public class ContactUsPage {
    private final WebDriver driver;

    private final By contactUsLink = By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'contact')]");
    private final By getInTouchHeader = By.xpath("//h2[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'get in touch')]");

    private final By nameInput = By.name("name");
    private final By emailInput = By.name("email");
    private final By subjectInput = By.name("subject");
    private final By messageInput = By.name("message");
    private final By uploadFileInput = By.name("upload_file");
    private final By submitButton = By.xpath("//input[@type='submit' or @value='Submit']");

    // After submit a success message is shown on the page
    private final By successMessage = By.xpath("//*[contains(., 'Success! Your details have been submitted successfully')]");
    private final By homeButton = By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'home')]");

    public ContactUsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isContactUsLinkVisible() {
        try {
            WaitUtils.waitForVisibility(driver, contactUsLink);
            ReportManager.step("Contact Us link visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Contact Us link NOT visible");
            return false;
        }
    }

    public void goToContactUs() {
        WebElement link = WaitUtils.waitForClickable(driver, contactUsLink);
        link.click();
        ReportManager.step("Clicked Contact Us link");
        WaitUtils.waitForVisibility(driver, getInTouchHeader);
    }

    public boolean isGetInTouchVisible() {
        try {
            WaitUtils.waitForVisibility(driver, getInTouchHeader);
            ReportManager.step("GET IN TOUCH visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("GET IN TOUCH NOT visible");
            return false;
        }
    }

    public void enterName(String name) {
        WebElement el = WaitUtils.waitForVisibility(driver, nameInput);
        el.clear();
        el.sendKeys(name);
        ReportManager.step("Entered contact name: " + name);
    }

    public void enterEmail(String email) {
        WebElement el = WaitUtils.waitForVisibility(driver, emailInput);
        el.clear();
        el.sendKeys(email);
        ReportManager.step("Entered contact email: " + email);
    }

    public void enterSubject(String subject) {
        WebElement el = WaitUtils.waitForVisibility(driver, subjectInput);
        el.clear();
        el.sendKeys(subject);
        ReportManager.step("Entered contact subject: " + subject);
    }

    public void enterMessage(String message) {
        WebElement el = WaitUtils.waitForVisibility(driver, messageInput);
        el.clear();
        el.sendKeys(message);
        ReportManager.step("Entered contact message");
    }

    public void uploadFile(Path pathToFile) {
        WebElement input = WaitUtils.waitForVisibility(driver, uploadFileInput);
        input.sendKeys(pathToFile.toAbsolutePath().toString());
        ReportManager.step("Uploaded file: " + pathToFile);
    }

    public void clickSubmit() {
        WebElement btn = WaitUtils.waitForClickable(driver, submitButton);
        btn.click();
        ReportManager.step("Clicked Submit on contact form");
    }

    public void acceptAlertIfPresent() {
        try {
            WaitUtils.waitForVisibility(driver, submitButton, 5); // brief wait for alert to appear
            Alert alert = driver.switchTo().alert();
            alert.accept();
            ReportManager.step("Accepted browser alert");
        } catch (Exception e) {
            // no alert
        }
    }

    public boolean isSuccessMessageVisible() {
        try {
            WaitUtils.waitForVisibility(driver, successMessage, 10);
            ReportManager.step("Contact form success message visible");
            return true;
        } catch (Exception e) {
            ReportManager.step("Contact form success message NOT visible");
            return false;
        }
    }

    public void clickHome() {
        WebElement btn = WaitUtils.waitForClickable(driver, homeButton);
        btn.click();
        ReportManager.step("Clicked Home button");
    }
}
