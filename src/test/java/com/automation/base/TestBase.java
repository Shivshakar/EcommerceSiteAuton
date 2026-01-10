package com.automation.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import com.automation.utils.WebDriverFactory;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class TestBase {
    protected WebDriver driver;
    protected String baseUrl;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // Read config (config.properties on classpath, overridden by -D system properties)
        this.baseUrl = ConfigReader.getBaseUrl();

        // Setup driver; if it fails, mark test as skipped
        try {
            WebDriverManager.chromedriver().setup();
            this.driver = WebDriverFactory.createDriver();
            this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to setup WebDriver: " + e.getMessage(), e);
        }
    }

//    @AfterMethod(alwaysRun = true)
//    public void tearDown() {
//        if (this.driver != null) {
//            this.driver.quit();
//        }
//    }
}
