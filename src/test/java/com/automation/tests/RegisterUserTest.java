package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.HomePage;
import com.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegisterUserTest extends TestBase {

    @Test
    public void testRegisterUser() {
        HomePage home = new HomePage(driver);
        home.goTo(baseUrl);
        home.clickSignupLogin();

        SignupPage signup = new SignupPage(driver);
        String uniqueName = "testuser" + System.currentTimeMillis();
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        signup.enterNameAndEmail(uniqueName, uniqueEmail);
        signup.clickSignupButton();

        signup.fillAccountInformation(true, "P@ssw0rd!",
                "1", "1", "1990",
                true, true,
                "Test", "User", "ACME Corp",
                "123 Main St", "Suite 1", "United States",
                "California", "Los Angeles", "90001", "1234567890");

        signup.clickCreateAccount();

        Assert.assertTrue(signup.isAccountCreated(), "Expected account to be created");
    }
}
