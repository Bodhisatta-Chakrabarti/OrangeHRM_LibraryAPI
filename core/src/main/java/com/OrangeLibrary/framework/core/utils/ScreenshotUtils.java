package com.OrangeLibrary.framework.core.utils;

import com.OrangeLibrary.framework.core.driver.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots from the current thread's WebDriver.
 * Screenshots are saved under test-output/screenshots/ with a
 * {testName}_{timestamp}.png naming convention.
 *
 * Designed to be called both on-demand from test code, and later
 * from a TestNG failure listener for automatic capture-on-failure.
 *
 * Usage:
 *   String path = ScreenshotUtils.capture("loginTest");
 */
public class ScreenshotUtils {

    private static final Logger logger= LogManager.getLogger(ScreenshotUtils.class);

    private static final String SCREENSHOT_DIR="test-output/screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT=DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtils()
    {

    }

    /**
     * Captures a screenshot of the current thread's browser state and saves it to disk.
     *
     * @param testName a descriptive name (usually the test method name) used in the filename
     * @return the absolute file path of the saved screenshot, or null if capture failed
     */
    public static String capture(String testName)
    {
        try{
            WebDriver driver= WebDriverFactory.getDriver();

            if (!(driver instanceof TakesScreenshot))
            {
                logger.warn("Current driver does not support screenshot capture");
                return null;
            }

            File sourceFile=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            Path outputDir= Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(outputDir))
            {
                Files.createDirectories(outputDir);
                logger.debug("Created screenshot output directory: {}", outputDir.toAbsolutePath());
            }

            String safeTestName=(testName==null || testName.trim().isEmpty())?"unnamed_test":testName.trim().replaceAll("[^a-zA-Z0-9_-]", "-");

            String timestamp= LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName=safeTestName + "_" + timestamp + ".png";

            Path destinationPath=outputDir.resolve(fileName);
            Files.copy(sourceFile.toPath(), destinationPath);

            String absolutePath=destinationPath.toAbsolutePath().toString();
            logger.info("Screenshot captured: {}", absolutePath);
            return absolutePath;

        }
        catch (IllegalStateException e) {
            // Thrown by WebDriverFactory.getDriver() if driver isn't initialized for this thread
            logger.warn("Could not capture screenshot - driver not initialized: {}", e.getMessage());
            return null;
        }
        catch (IOException e) {
            logger.warn("Failed to save screenshot to disk: {}", e.getMessage());
            return null;
        }
        catch (Exception e) {
            // Catch-all so a screenshot failure never masks or compounds the original test failure
            logger.warn("Unexpected error while capturing screenshot: {}", e.getMessage());
            return null;
        }
    }

}
