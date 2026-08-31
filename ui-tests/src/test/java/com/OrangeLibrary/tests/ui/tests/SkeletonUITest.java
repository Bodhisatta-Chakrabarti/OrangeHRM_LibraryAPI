package com.OrangeLibrary.tests.ui.tests;

import com.OrangeLibrary.framework.common.utils.ExcelDataReader;
import com.OrangeLibrary.framework.core.config.ConfigManager;
import com.OrangeLibrary.framework.core.driver.WebDriverFactory;
import com.OrangeLibrary.framework.core.utils.ScreenshotUtils;
import com.OrangeLibrary.framework.core.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SkeletonUITest {

    @BeforeMethod
    public void setUp()
    {
        WebDriverFactory.initDriver("ui");
    }

    @Test(retryAnalyzer = com.OrangeLibrary.framework.core.listeners.RetryAnalyzer.class)
    public void skeletonPlaceholderTest()
    {
        WebDriver driver=WebDriverFactory.getDriver();

        String baseUrl=ConfigManager.getProperty("ui", "base.url");
        driver.get(baseUrl);

        WebElement usernameField= WaitUtils.waitForVisibility(By.name("username"));
        Assert.assertTrue(usernameField.isDisplayed(), "Username field should be visible after explicit wait");

        String pageTitle= driver.getTitle();
        System.out.println("Page Title: " + pageTitle);

        //Assert.fail("Deliberate failure to validate retry logic");
        Assert.assertFalse(pageTitle.isEmpty(), "Page title should not be empty");

        //Assert.assertTrue(true);
//        System.out.println("Base URL: " + ConfigManager.getProperty("ui", "base.url"));
//        System.out.println("Browser: " + ConfigManager.getProperty("ui", "browser"));
        //System.out.println("Username loaded: " + ConfigManager.getSecret("ADMIN_USERNAME"));
    }

    @DataProvider(name = "loginData")
    public Object[][] loginData()
    {
        return ExcelDataReader.readSheet("testdata/logindata.xlsx", "Sheet1");
    }

    @Test(dataProvider = "loginData")
    public void validateExcelDataReader(String username, String password, String expectedResult)
    {
        System.out.println("Row read from Excel -> username: " + username +
                ", password: " + password + ", expectedResult: " + expectedResult);

        WebDriver driver=WebDriverFactory.getDriver();
        String baseUrl=ConfigManager.getProperty("ui", "base.url");
        driver.get(baseUrl);

        // Just validating data flows correctly into the test - not asserting login success/failure yet,
        // since actual login interaction belongs in the Page Object we build next.
        Assert.assertNotNull(username, "Username value should not be null (even if string is blank)");
        Assert.assertNotNull(expectedResult, "Expected result should not be null");
    }

    @AfterMethod
    public void tearDown()
    {
        ScreenshotUtils.capture("skeletonPlaceholderTest");
        WebDriverFactory.quitDriver();
    }

}
