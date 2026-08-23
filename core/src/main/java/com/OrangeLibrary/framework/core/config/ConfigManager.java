package com.OrangeLibrary.framework.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and provides access to environment-specific configuration properties.
 * Config files are expected on the classpath at: config/{env}.properties
 * Environment is selected via the JVM system property "env" (default: qa).

 * Usage:
 *   ConfigManager.getProperty("ui", "base.url");
 *   ConfigManager.getProperty("api", "api.base.url");
 */
public class ConfigManager {

    private static final String DEFAULT_ENV="qa";
    private static final String CONFIG_FOLDER="config";

    // Cache: one Properties object per module context ("ui", "api"), loaded once per JVM run
    private static final ConcurrentHashMap<String, Properties> propertiesCache=new ConcurrentHashMap<>();

    // Private constructor to prevent instantiation - this is a static utility class
    private ConfigManager()
    {

    }

    /**
     * Fetches a property value for the given module context and key.
     *
     * @param moduleContext "ui" or "api" - determines which classpath's config file to read
     * @param key           the property key to look up
     * @return the property value
     * @throws RuntimeException if the config file is missing or the key is not found
     */
    public static String getProperty(String moduleContext, String key)
    {
        Properties properties=propertiesCache.computeIfAbsent(moduleContext, ConfigManager::loadProperties);
        String value=properties.getProperty(key);
        if (value==null || value.trim().isEmpty())
        {
            throw new RuntimeException("Property " + key + "not found in config for module context " + moduleContext
            + "and environment " + getActiveEnvironment() + ". Check that the key exists in the corresponding properties file");
        }
        return  value;
    }

    /**
     * Fetches a property value, returning a default if the key is not found,
     * instead of throwing. Use sparingly - prefer getProperty() for required config.
     */
    public static String getProperty(String moduleContext, String key, String defaultValue)
    {
        Properties properties=propertiesCache.computeIfAbsent(moduleContext, ConfigManager::loadProperties);
        String value=properties.getProperty(key, defaultValue);
        if (value==null || value.trim().isEmpty())
        {
            throw new RuntimeException("Property " + key + "not found in config for module context " + moduleContext
                    + "and environment " + getActiveEnvironment() + ". Check that the key exists in the corresponding properties file");
        }
        return value;
    }

    /**
     * Returns the currently active environment name (e.g., "qa", "dev", "staging", "prod"),
     * resolved from the "env" system property, defaulting to DEFAULT_ENV if not set.
     */
    private static String getActiveEnvironment() {
        String env=System.getProperty("env");
        return (env==null || env.trim().isEmpty())?DEFAULT_ENV:env;
    }

    /**
     * Loads the properties file for the given module context from the classpath.
     * Path resolved as: config/{env}.properties
     */
    private static Properties loadProperties(String moduleContext) {
        String env=getActiveEnvironment();
        String resourcePath=CONFIG_FOLDER + "/" + env + ".properties";
        Properties properties=new Properties();

        try(InputStream inputStream=ConfigManager.class.getClassLoader().getResourceAsStream(resourcePath)){
            if (inputStream==null)
            {
                throw new RuntimeException("Config file not found on classpath " + resourcePath + " for module context "
                + moduleContext + ". Verify the file exists under src/test/resources/config in the " + moduleContext + " module.");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: '" + resourcePath
                    + "' for module context '" + moduleContext + "'.", e
            );
        }

        return properties;
    }

    public static String getSecret(String key)
    {
        String value=System.getenv(key);
        if (value==null || value.trim().isEmpty())
        {
            throw new RuntimeException("Environment variable " + key + " is not set. \" +\n" +
                    "            \"Required secrets must be set as environment variables before running tests.");
        }
        return value;
    }

}
