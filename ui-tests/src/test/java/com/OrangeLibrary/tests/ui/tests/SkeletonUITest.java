package com.OrangeLibrary.tests.ui.tests;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import com.OrangeLibrary.framework.core.driver.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SkeletonUITest {

    @BeforeMethod
    public void setUp()
    {
        WebDriverFactory.initDriver("ui");
    }

    @Test
    public void skeletonPlaceholderTest()
    {
        WebDriver driver=WebDriverFactory.getDriver();

        String baseUrl=ConfigManager.getProperty("ui", "base.url");
        driver.get(baseUrl);

        String pageTitle= driver.getTitle();
        System.out.println("Page Title: " + pageTitle);

        Assert.assertFalse(pageTitle.isEmpty(), "Page title should not be empty");
        //Assert.assertTrue(true);
//        System.out.println("Base URL: " + ConfigManager.getProperty("ui", "base.url"));
//        System.out.println("Browser: " + ConfigManager.getProperty("ui", "browser"));
        //System.out.println("Username loaded: " + ConfigManager.getSecret("ADMIN_USERNAME"));
    }

    @AfterMethod
    public void tearDown()
    {
        WebDriverFactory.quitDriver();
    }

}
