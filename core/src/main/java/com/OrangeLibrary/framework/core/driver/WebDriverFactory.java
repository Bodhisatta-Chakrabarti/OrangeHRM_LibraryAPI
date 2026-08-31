package com.OrangeLibrary.framework.core.driver;

import com.OrangeLibrary.framework.common.constants.BrowserType;
import com.OrangeLibrary.framework.core.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Manages WebDriver lifecycle per thread, enabling safe parallel test execution.
 * Browser selection precedence: -Dbrowser system property > config file "browser" key.
 *
 * Usage:
 *   WebDriverFactory.initDriver("ui"); // creates and stores driver for current thread
 *   WebDriver driver = WebDriverFactory.getDriver(); // retrieves current thread's driver
 *   WebDriverFactory.quitDriver(); // quits and cleans up current thread's driver
 */
public class WebDriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal=new ThreadLocal<>();

    private static final Logger logger= LogManager.getLogger(WebDriverFactory.class);

    private WebDriverFactory()
    {

    }

    /**
     * Initializes a WebDriver instance for the current thread based on resolved browser type
     * and config-driven settings (headless, timeouts), and stores it in ThreadLocal.
     *
     * @param moduleContext the config module context to read settings from (e.g., "ui")
     */
    public static void initDriver(String moduleContext)
    {
        if (driverThreadLocal.get()!=null)
        {
            throw new IllegalStateException("WebDriver is already initialized for this thread. Call quitDriver() first.");
        }

        BrowserType browserType=resolveBrowserType(moduleContext);
        boolean headless=Boolean.parseBoolean(ConfigManager.getProperty(moduleContext, "headless", "false"));

        int implicitWaitSeconds=Integer.parseInt(ConfigManager.getProperty(moduleContext, "implicit.wait.seconds", "10"));
        int pageLoadTimeoutSeconds=Integer.parseInt(ConfigManager.getProperty(moduleContext, "page.load.timeout.seconds", "30"));

        WebDriver driver=null;
        logger.info("Initializing WebDriver - browser: {}, headless: {}", browserType, headless);

        try{
            driver=createDriver(browserType, headless);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSeconds));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeoutSeconds));
            driver.manage().window().maximize();

            driverThreadLocal.set(driver);
            logger.debug("WebDriver initialized and stored for thread: {}", Thread.currentThread().getName());
        }catch (RuntimeException e)
        {
            if (driver!=null)
            {
                driver.quit();
            }
            throw e;
        }
    }

    /**
     * Returns the WebDriver instance for the current thread.
     *
     * @throws IllegalStateException if called before initDriver()
     */
    public static WebDriver getDriver()
    {
        WebDriver driver=driverThreadLocal.get();
        if (driver==null)
        {
            logger.error("getDriver() called before initDriver() on thread: {}", Thread.currentThread().getName());
            throw new IllegalStateException("WebDriver has not been initialized for this thread. Call initDriver() first");
        }
        return driver;
    }

    /**
     * Quits the current thread's WebDriver instance and removes it from ThreadLocal.
     * Must be called after each test to prevent memory leaks and browser process buildup.
     */
    public static void quitDriver()
    {
        WebDriver driver=driverThreadLocal.get();
        try{
            if (driver!=null)
            {
                driver.quit();
                logger.debug("WebDriver quit and removed for thread: {}", Thread.currentThread().getName());
            }
        }finally {
            driverThreadLocal.remove();
        }
    }

    /**
     * Creates the appropriate WebDriver instance based on browser type, with headless
     * configuration applied per browser's specific options syntax.
     */
    private static WebDriver createDriver(BrowserType browserType, boolean headless) {
        switch (browserType){
            case CHROME :
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions=new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                if (headless)
                {
                    chromeOptions.addArguments("--headless=new");
                }
                ChromeDriverService chromeService=new ChromeDriverService.Builder().withTimeout(Duration.ofSeconds(120)).build();
                return new ChromeDriver(chromeService, chromeOptions);
            case FIREFOX :
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions=new FirefoxOptions();
                if (headless)
                {
                    firefoxOptions.addArguments("-headless");
                }
                return new FirefoxDriver(firefoxOptions);
            case EDGE :
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions=new EdgeOptions();
                edgeOptions.addArguments("--remote-allow-origins=*");
                edgeOptions.addArguments("--disable-gpu");
                edgeOptions.addArguments("--disable-dev-shm-usage");
                edgeOptions.addArguments("--no-sandbox");
                if (headless)
                {
                    edgeOptions.addArguments("--headless=new");
                }
                return new EdgeDriver(edgeOptions);
            default :
                logger.error("Unsupported browser type requested: {}", browserType);
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    /**
     * Resolves the browser type using precedence: -Dbrowser system property > config file.
     */
    private static BrowserType resolveBrowserType(String moduleContext) {
        String systemPropertyBrowser=System.getProperty("browser");
        if (systemPropertyBrowser!=null && !systemPropertyBrowser.trim().isEmpty())
        {
            logger.debug("Browser resolved from system property: {}", systemPropertyBrowser);
            return BrowserType.fromString(systemPropertyBrowser);
        }

        String configBrowser= ConfigManager.getProperty(moduleContext, "browser");
        logger.debug("Browser resolved from config file: {}", configBrowser);
        return BrowserType.fromString(configBrowser);
    }

}
