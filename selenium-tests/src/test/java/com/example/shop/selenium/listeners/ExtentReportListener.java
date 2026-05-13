package com.example.shop.selenium.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.example.shop.selenium.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static ExtentReports extent;
    private static Path reportDir;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    @Override
    public void onStart(ISuite suite) {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        reportDir = Paths.get("target", "extent-report");
        try {
            Files.createDirectories(reportDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Path reportFile = reportDir.resolve("ExtentReport-" + stamp + ".html");
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile.toFile());
        spark.config().setTheme(Theme.DARK);
        spark.config().setReportName("shop.io Selenium Suite");
        spark.config().setDocumentTitle("Selenium Test Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Suite", suite.getName());
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(
                result.getTestClass().getName() + "::" + result.getMethod().getMethodName());
        CURRENT.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = CURRENT.get();
        if (test != null) test.log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = CURRENT.get();
        if (test == null) return;
        test.log(Status.FAIL, result.getThrowable());
        String screenshotPath = captureScreenshot(result.getMethod().getMethodName());
        if (screenshotPath != null) {
            test.addScreenCaptureFromPath(screenshotPath);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = CURRENT.get();
        if (test != null) test.log(Status.SKIP, "Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        // no-op; per-suite onFinish flushes
    }

    private String captureScreenshot(String name) {
        try {
            WebDriver driver = DriverFactory.get();
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dst = reportDir.resolve("screenshots").resolve(
                    name + "-" + System.currentTimeMillis() + ".png");
            Files.createDirectories(dst.getParent());
            Files.copy(src.toPath(), dst);
            return dst.toAbsolutePath().toString();
        } catch (Exception e) {
            return null;
        }
    }
}
