package com.OrangeLibrary.framework.common.constants;

/**
 * Supported browser types for UI test execution.
 * Used by WebDriverFactory to determine which WebDriver implementation to instantiate.
 */
public enum BrowserType {

    CHROME,
    FIREFOX,
    EDGE;

    /**
     * Safely converts a string (e.g., from config file or system property) into a BrowserType.
     * Case-insensitive, trims whitespace.
     *
     * @param value the string value to convert (e.g., "chrome", "Chrome", "CHROME")
     * @return the matching BrowserType
     * @throws IllegalArgumentException if the value doesn't match any supported browser
     */
    public static BrowserType fromString(String value)
    {
        if (value==null || value.trim().isEmpty())
        {
            throw new IllegalArgumentException("Browser Type value is null or empty. Supported values: CHROME, FIREFOX, EDGE");
        }
        try{
            return BrowserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unsupported Browser Type: " + value + ". Supported values: CHROME, FIREFOX, EDGE", e);
        }
    }

}
