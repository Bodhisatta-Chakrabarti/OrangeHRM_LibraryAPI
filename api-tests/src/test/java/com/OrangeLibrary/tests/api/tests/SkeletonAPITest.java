package com.OrangeLibrary.tests.api.tests;

import com.OrangeLibrary.framework.core.api.ApiRequestBuilder;
import com.OrangeLibrary.framework.core.config.ConfigManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SkeletonAPITest {

    @Test
    public void skeletonPlaceholderTest() {
        Response response = new ApiRequestBuilder().endpoint("/api/v1/Books").log().get();
        int statusCode= response.statusCode();
        String responseBody=response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);

        Assert.assertEquals(statusCode, 200, "Expected status code 200 for GET /api/v1/Books");

        //Assert.assertTrue(true);
//        System.out.println("API Base URL: " + ConfigManager.getProperty("api", "api.base.url"));
//        System.out.println("API Retry Count: " + ConfigManager.getProperty("api", "api.retry.count"));
    }

}