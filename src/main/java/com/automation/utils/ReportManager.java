package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ReportManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> tlTest = new ThreadLocal<>();

    // initialize reports (call once per suite)
    public static synchronized void init(String reportPath) {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    // create a test and store it in ThreadLocal
    public static ExtentTest createTest(String name) {
        if (extent == null) {
            throw new IllegalStateException("ExtentReports not initialized. Call ReportManager.init(...) first.");
        }
        ExtentTest test = extent.createTest(name);
        tlTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return tlTest.get();
    }

    public static void removeTest() {
        tlTest.remove();
    }

    // helper for step logging
    public static void step(String message) {
        ExtentTest t = getTest();
        if (t != null) {
            t.log(Status.INFO, message);
        }
    }

    public static void pass(String message) {
        ExtentTest t = getTest();
        if (t != null) {
            t.log(Status.PASS, message);
        }
    }

    public static void fail(String message) {
        ExtentTest t = getTest();
        if (t != null) {
            t.log(Status.FAIL, message);
        }
    }
}

