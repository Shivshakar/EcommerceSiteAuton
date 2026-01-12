package com.automation.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import com.automation.utils.WebDriverFactory;
import com.automation.utils.ConfigReader;
import com.automation.utils.ReportManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.MediaEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class TestBase {
    protected WebDriver driver;
    protected String baseUrl;

    // Extent reports handled by ReportManager

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        // initialize ExtentReports via ReportManager
        Path reportDir = Paths.get("target", "extent-report");
        try {
            Files.createDirectories(reportDir);
        } catch (IOException ignored) {}
        String reportPath = reportDir.resolve("extent.html").toString();
        ReportManager.init(reportPath);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        // Read config (config.properties on classpath, overridden by -D system properties)
        this.baseUrl = ConfigReader.getBaseUrl();

        // Create an Extent test for this method using ReportManager
        ReportManager.createTest(method.getName());

        // Setup driver; if it fails, mark test as skipped
        try {
            WebDriverManager.chromedriver().setup();
            this.driver = WebDriverFactory.createDriver();
            this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            ReportManager.step("WebDriver initialized");
        } catch (Throwable e) {
            ReportManager.fail("Failed to setup WebDriver: " + e.getMessage());
            throw new RuntimeException("Failed to setup WebDriver: " + e.getMessage(), e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Log result to Extent via ReportManager
        if (result.getStatus() == ITestResult.FAILURE) {
            // capture screenshot
            try {
                String screenshot = takeScreenshot(result.getName());
                ReportManager.getTest().fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
            } catch (Exception e) {
                ReportManager.getTest().fail(result.getThrowable());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            ReportManager.pass("Test passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            ReportManager.getTest().skip(result.getThrowable());
        }

        ReportManager.removeTest();

        if (this.driver != null) {
            try {
                this.driver.quit();
            } catch (Exception ignored) {}
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ReportManager.flush();
    }

    // Helper to take screenshot and return the path (relative to project root)
    private String takeScreenshot(String name) throws IOException {
        if (driver == null) return null;
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path screenshotsDir = Paths.get("target", "extent-report", "screenshots");
        Files.createDirectories(screenshotsDir);
        String fileName = name + "-" + System.currentTimeMillis() + ".png";
        Path dest = screenshotsDir.resolve(fileName);
        Files.copy(src.toPath(), dest);
        return dest.toString();
    }
}