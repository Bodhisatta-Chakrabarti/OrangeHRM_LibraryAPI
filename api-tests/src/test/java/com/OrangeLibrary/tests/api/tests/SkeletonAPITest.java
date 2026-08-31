package com.OrangeLibrary.tests.api.tests;

import com.OrangeLibrary.framework.common.models.Book;
import com.OrangeLibrary.framework.common.utils.JsonDataReader;
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
        //String responseBody=response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
//        System.out.println("Response Body: " + responseBody);

        Assert.assertEquals(statusCode, 200, "Expected status code 200 for GET /api/v1/Books");

        //Assert.assertTrue(true);
//        System.out.println("API Base URL: " + ConfigManager.getProperty("api", "api.base.url"));
//        System.out.println("API Retry Count: " + ConfigManager.getProperty("api", "api.retry.count"));
    }

    @Test
    public void validateJsonDataReader()
    {
        Book book= JsonDataReader.readFromFile("testdata/createBookPayload.json", Book.class);
        System.out.println("Book loaded from JSON: " + book);

        Assert.assertNotNull(book.getTitle(), "Title should not be null");
        Assert.assertEquals(book.getPageCount(), 250, "Page count should match test data");

        //Use the loaded data to actually create a book via API - validates the full chain
        Response response=new ApiRequestBuilder().endpoint("/api/v1/Books").body(book).log().post();
        System.out.println("Create book response status: " + response.getStatusCode());
        System.out.println("Create book response body: " + response.getBody().asString());
        Assert.assertTrue(response.getStatusCode()==200 || response.getStatusCode()==201,
                "Expected 200 or 201 for book creation");
    }

}