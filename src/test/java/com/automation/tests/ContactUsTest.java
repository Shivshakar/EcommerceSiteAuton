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

        // 1-3: navigate to home and verify Contact Us link is visible
        driver.get(baseUrl);
        Assert.assertTrue(contact.isContactUsLinkVisible(), "Contact Us link should be visible on home page");

        // 4: Click Contact Us
        contact.goToContactUs();
        Assert.assertTrue(contact.isGetInTouchVisible(), "GET IN TOUCH should be visible after clicking Contact Us");

        // 6: fill name, email, subject, message
        contact.enterName("Test User");
        contact.enterEmail("test.contact@example.com");
        contact.enterSubject("Automation Test");
        contact.enterMessage("This is a test message for contact us form.");

        // 7: upload file
        contact.uploadFile(Paths.get("src/test/resources/upload.txt"));

        // 8: Click Submit
        contact.clickSubmit();

        // 9: Accept OK button (browser alert)
        contact.acceptAlertIfPresent();

        // 10: Verify success message
        Assert.assertTrue(contact.isSuccessMessageVisible(), "Success message should be visible after submitting contact form");

        // 11: Click Home and verify we navigate back (we'll assert home button action does not throw)
        contact.clickHome();
        // basic smoke: ensure page loaded again by checking the presence of contact link or similar
        Assert.assertTrue(contact.isContactUsLinkVisible(), "Home page should show Contact Us link after returning home");
    }
}
