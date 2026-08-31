package com.OrangeLibrary.framework.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads JSON test data files from the classpath and deserializes them into POJOs.
 * Used primarily for API test payloads (e.g., testdata/createBookPayload.json).
 *
 * Usage:
 *   Book book = JsonDataReader.readFromFile("testdata/createBookPayload.json", Book.class);
 */
public class JsonDataReader {

    private static final Logger logger= LogManager.getLogger(JsonDataReader.class);
    private static final ObjectMapper mapper=new ObjectMapper();

    private JsonDataReader()
    {

    }

    /**
     * Reads a JSON file from the classpath and deserializes it into the given type.
     *
     * @param resourcePath classpath-relative path to the JSON file (e.g., "testdata/createBookPayload.json")
     * @param type         the target class to deserialize into
     * @param <T>          the type parameter
     * @return an instance of T populated from the JSON file
     */
    public static <T> T readFromFile(String resourcePath, Class<T> type)
    {
        logger.debug("Reading JSON test data from: {}", resourcePath);
        try (InputStream inputStream=JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)){
            if (inputStream==null)
            {
                logger.error("JSON test data file not found on classpath: {}", resourcePath);
                throw new RuntimeException("JSON test data file not found on classpath: " + resourcePath + ". " +
                        "Verify the file exists under src/test/resources");
            }

            T result=mapper.readValue(inputStream, type);
            logger.debug("Successfully deserialized JSON into: {}", type.getSimpleName());
            return result;
        } catch (IOException e) {
            logger.error("Failed to parse JSON test data file '{}': {}", resourcePath, e.getMessage());
            throw new RuntimeException("Failed to parse JSON test data file: " + resourcePath + ".", e);
        }
    }

}
