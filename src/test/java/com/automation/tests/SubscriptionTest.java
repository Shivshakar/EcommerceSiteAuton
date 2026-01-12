package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.HomePage;
import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriptionTest extends TestBase {

    @Test
    public void testVerifySubscriptionOnHomePage() {
        HomePage home = new HomePage(driver);
        home.goTo(baseUrl);

        // use page object methods rather than inline locators
        Assert.assertNotNull(home.getSubscriptionHeading(), "Subscription heading should be visible in footer");
        ReportManager.step("Subscription heading visible");

        String email = "sub" + System.currentTimeMillis() + "@example.com";
        home.enterSubscriptionEmail(email);
        home.clickSubscribe();

        String msg = null;
        try {
            msg = home.getSubscriptionSuccessMessage().getText();
        } catch (Exception ignored) {
            // will assert below
        }

        Assert.assertNotNull(msg, "Subscription success message should be visible");
        ReportManager.step("Subscription success message: " + msg);
    }
}
