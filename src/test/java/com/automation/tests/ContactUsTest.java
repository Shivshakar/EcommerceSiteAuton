package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.ContactUsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class ContactUsTest extends TestBase {

    @Test
    public void testContactUsForm() {
        ContactUsPage contact = new ContactUsPage(driver);

        driver.get(baseUrl);
        Assert.assertTrue(contact.isContactUsLinkVisible(), "Contact Us link should be visible on home page");

        contact.goToContactUs();
        Assert.assertTrue(contact.isGetInTouchVisible(), "GET IN TOUCH should be visible after clicking Contact Us");

        contact.enterName("Test User");
        contact.enterEmail("test.contact@example.com");
        contact.enterSubject("Automation Test");
        contact.enterMessage("This is a test message for contact us form.");

        contact.uploadFile(Paths.get("src/test/resources/upload.txt"));

        contact.clickSubmit();

        contact.acceptAlertIfPresent();

        Assert.assertTrue(contact.isSuccessMessageVisible(), "Success message should be visible after submitting contact form");

        contact.clickHome();
        Assert.assertTrue(contact.isContactUsLinkVisible(), "Home page should show Contact Us link after returning home");
    }
}
