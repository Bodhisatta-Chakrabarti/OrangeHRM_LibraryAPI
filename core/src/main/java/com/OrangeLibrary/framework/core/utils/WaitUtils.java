package com.OrangeLibrary.framework.core.utils;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import com.OrangeLibrary.framework.core.driver.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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

    private static final Logger logger= LogManager.getLogger(WaitUtils.class);

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
        logger.debug("Waiting for visibility of element: {} (timeout: {}s)", locator, timeoutSeconds);
        try{
            return buildWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
        }catch (TimeoutException e)
        {
            logger.warn("Timed out waiting for visibility of element: {} after {}s", locator, timeoutSeconds);
            throw e;
        }
    }

    public static WebElement waitForClickability(By locator)
    {
        return waitForClickability(locator, getDefaultTimeoutSeconds());
    }

    public static WebElement waitForClickability(By locator, int timeoutSeconds)
    {
        logger.debug("Waiting for clickability of element: {} (timeout: {}s)", locator, timeoutSeconds);
        try{
            return buildWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
        }catch (TimeoutException e)
        {
            logger.warn("Timed out waiting for clickability of element: {} after {}s", locator, timeoutSeconds);
            throw e;
        }
    }

    public static WebElement waitForPresence(By locator)
    {
        return waitForPresence(locator, getDefaultTimeoutSeconds());
    }

    public static WebElement waitForPresence(By locator, int timeoutSeconds)
    {
        logger.debug("Waiting for presence of element: {} (timeout: {}s)", locator, timeoutSeconds);
        try{
            return buildWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
        }catch (TimeoutException e)
        {
            logger.warn("Timed out waiting for presence of element: {} after {}s", locator, timeoutSeconds);
            throw e;
        }
    }

    public static boolean waitForInvisibility(By locator)
    {
        return waitForInvisibility(locator, getDefaultTimeoutSeconds());
    }

    public static boolean waitForInvisibility(By locator, int timeoutSeconds)
    {
        logger.debug("Waiting for invisibility of element: {} (timeout: {}s)", locator, timeoutSeconds);
        try{
            return buildWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        }catch (TimeoutException e)
        {
            logger.warn("Timed out waiting for invisibility of element: {} after {}s", locator, timeoutSeconds);
            throw e;
        }
    }

    public static boolean waitForTextToBePresent(By locator, String text)
    {
        return waitForTextToBePresent(locator, text, getDefaultTimeoutSeconds());
    }

    public static boolean waitForTextToBePresent(By locator, String text, int timeoutSeconds)
    {
        logger.debug("Waiting for text '{}' in element: {} (timeout: {}s)", text, locator, timeoutSeconds);
        try{
            return buildWait(timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        }catch (TimeoutException e)
        {
            logger.warn("Timed out waiting for text '{}' in element: {} (timeout: {}s)", text, locator, timeoutSeconds);
            throw e;
        }
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
