package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.HomePage;
import com.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SignupLoginUserTest extends TestBase {

    // store credentials so dependent test can use them
    private static String registeredEmail;
    private static final String registeredPassword = "P@ssw0rd!";

    // shared HomePage instance to avoid repeating navigation/setup in each test
    private HomePage home;

    @BeforeMethod
    public void openLoginPage() {
        home = new HomePage(driver);
        home.goTo(baseUrl);
        home.clickSignupLogin();
    }

    @Test
    public void testRegisterUser() {
        SignupPage signup = new SignupPage(driver);
        String uniqueName = "testuser" + System.currentTimeMillis();
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        signup.enterNameAndEmail(uniqueName, uniqueEmail);
        signup.clickSignupButton();

        signup.fillAccountInformation(true, registeredPassword,
                "1", "1", "1990",
                true, true,
                "Test", "User", "ACME Corp",
                "123 Main St", "Suite 1", "United States",
                "California", "Los Angeles", "90001", "1234567890");

        signup.clickCreateAccount();

        Assert.assertTrue(signup.isAccountCreated(), "Expected account to be created");

        // persist the registered email for the login test
        registeredEmail = uniqueEmail;
    }

    // New negative test: attempting to register again with the same email should show an error
    @Test(dependsOnMethods = {"testRegisterUser"})
    public void testRegisterUserWithExistingEmail() {
        Assert.assertNotNull(registeredEmail, "Registered email should be set by testRegisterUser");

        SignupPage signup = new SignupPage(driver);
        // use a different name but the same email to attempt duplicate registration
        String anotherName = "another" + System.currentTimeMillis();
        signup.enterNameAndEmail(anotherName, registeredEmail);
        signup.clickSignupButton();

        // verify the page shows an 'email already exist' message
        Assert.assertTrue(signup.isEmailAlreadyExist(), "Signup should show 'email already exist' error for duplicate email");
    }

    @Test(dependsOnMethods = {"testRegisterUser"})
    public void testLoginUserWithCorrectEmailAndPassword() {
        // Use the registered credentials from the previous test
        Assert.assertNotNull(registeredEmail, "Registered email should be set by testRegisterUser");

        home.enterLoginEmail(registeredEmail);
        home.enterLoginPassword(registeredPassword);
        home.clickLoginButton();

        // Verify successful login
        Assert.assertTrue(home.isLoggedIn(), "User should be logged in with registered credentials");
    }

    // New negative test: invalid credentials should not log the user in
    @Test
    public void testLoginWithInvalidCredentials() {
        // use an obviously invalid email/password
        home.enterLoginEmail("invalid_" + System.currentTimeMillis() + "@example.com");
        home.enterLoginPassword("wrongPassword123");
        home.clickLoginButton();

        // Verify login did not succeed
        Assert.assertFalse(home.isLoggedIn(), "User should NOT be logged in with invalid credentials");
    }

    // New test: logout after successful login
    @Test(dependsOnMethods = {"testRegisterUser"})
    public void testLogoutAfterLogin() {

        home.enterLoginEmail(registeredEmail);
        home.enterLoginPassword(registeredPassword);
        home.clickLoginButton();
        // Ensure we're logged in
        Assert.assertTrue(home.isLoggedIn(), "Precondition: user should be logged in");

        // Click logout and verify logged out state
        home.clickLogout();
        Assert.assertTrue(home.isLoggedOut(), "User should be logged out after clicking Logout");
    }
}
