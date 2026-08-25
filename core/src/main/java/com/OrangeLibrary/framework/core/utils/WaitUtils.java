package com.OrangeLibrary.framework.core.utils;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import com.OrangeLibrary.framework.core.driver.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Explicit wait helpers for UI tests. Each method internally resolves the current
 * thread's WebDriver via WebDriverFactory and re-locates elements via By locators
 * (not cached WebElement references) so waits remain reliable against stale elements.
 *
 * Default timeout is read from config key "explicit.wait.seconds" (module context: "ui").
 * Each method has an overload accepting an explicit timeout for edge cases.
 *
 * Usage:
 *   WebElement el = WaitUtils.waitForVisibility(By.id("username"));
 *   WaitUtils.waitForClickability(By.id("submit")).click();
 */
public class WaitUtils {

    private static final String MODULE_CONTEXT="ui";

    private WaitUtils()
    {

    }

    public static WebElement waitForVisibility(By locator)
    {
        return waitForVisibility(locator, getDefaultTimeoutSeconds());
    }

    public static WebElement waitForVisibility(By locator, int timeoutSeconds)
    {
        return buildWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(By locator)
    {
        return waitForClickability(locator, getDefaultTimeoutSeconds());
    }

    public static WebElement waitForClickability(By locator, int timeoutSeconds)
    {
        return buildWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(By locator)
    {
        return waitForPresence(locator, getDefaultTimeoutSeconds());
    }

    public static WebElement waitForPresence(By locator, int timeoutSeconds)
    {
        return buildWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForInvisibility(By locator)
    {
        return waitForInvisibility(locator, getDefaultTimeoutSeconds());
    }

    public static boolean waitForInvisibility(By locator, int timeoutSeconds)
    {
        return buildWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForTextToBePresent(By locator, String text)
    {
        return waitForTextToBePresent(locator, text, getDefaultTimeoutSeconds());
    }

    public static boolean waitForTextToBePresent(By locator, String text, int timeoutSeconds)
    {
        return buildWait(timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Reads the default explicit wait timeout from config (module context: "ui").
     */
    private static int getDefaultTimeoutSeconds() {
        return Integer.parseInt(ConfigManager.getProperty(MODULE_CONTEXT, "explicit.wait.seconds", "15"));
    }

    /**
     * Builds a fresh WebDriverWait instance against the current thread's driver.
     */
    private static WebDriverWait buildWait(int timeoutSeconds) {
        WebDriver driver= WebDriverFactory.getDriver();
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

}
