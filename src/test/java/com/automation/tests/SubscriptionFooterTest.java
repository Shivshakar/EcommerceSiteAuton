package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.HomePage;
import com.automation.utils.ReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriptionFooterTest extends TestBase {

    @Test
    public void testSubscriptionFromFooter() {
        HomePage home = new HomePage(driver);

        // 1-3: Launch browser and navigate to the site, verify home page visible via signup/login link presence
        home.goTo(baseUrl);
        Assert.assertNotNull(home.getSubscriptionHeading(), "Home page should be visible and subscription heading present");
        ReportManager.step("Home page visible and subscription heading found");

        // 4: Click 'Cart' button - optional if not present, skip gracefully
        // Move locator logic to the page object and call the helper
        home.clickCart();

        // 5: Scroll down to footer
        home.scrollToFooter();

        // 6: Verify text 'SUBSCRIPTION'
        String headingText = home.getSubscriptionHeading().getText();
        Assert.assertTrue(headingText.toLowerCase().contains("subscription"), "Footer should contain 'SUBSCRIPTION'");
        ReportManager.step("Verified subscription heading text: " + headingText);

        // 7: Enter email address and click arrow button
        String email = "sub" + System.currentTimeMillis() + "@example.com";
        home.enterSubscriptionEmail(email);
        home.clickSubscribe();

        // 8: Verify success message contains expected text
        String expected = "You have been successfully subscribed!";
        String msg = null;
        try {
            msg = home.getSubscriptionSuccessMessage().getText().trim();
        } catch (Exception ignored) {
            // will assert below
        }

        Assert.assertTrue(msg.contains(expected), "Subscription success message should contain expected text. Actual: " + msg);
        ReportManager.step("Subscription success message matched: " + msg);
    }
}
