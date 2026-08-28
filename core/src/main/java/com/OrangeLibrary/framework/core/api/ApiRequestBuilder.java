package com.OrangeLibrary.framework.core.api;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for constructing and executing REST Assured API requests.
 * Automatically resolves base URI and default settings from config (module context: "api").
 *
 * Usage:
 *   Response response = new ApiRequestBuilder()
 *          .endpoint("/books/{id}")
 *          .pathParam("id", "42")
 *          .header("X-Custom-Header", "value")
 *          .get();
 *
 *   Response response = new ApiRequestBuilder()
 *          .endpoint("/books")
 *          .body(newBookPayload)
 *          .post();
 */
public class ApiRequestBuilder {

    private static final Logger logger= LogManager.getLogger(ApiRequestBuilder.class);

    private static final String MODULE_CONTEXT="api";

    private String endpoint="";
    private Object requestBody;
    private final Map<String, String> headers=new HashMap<>();
    private final Map<String, String> queryParams=new HashMap<>();
    private final Map<String, String> pathParams=new HashMap<>();
    private String bearerToken;
    private boolean logEnabled;

    public ApiRequestBuilder()
    {
        this.logEnabled=Boolean.parseBoolean(ConfigManager.getProperty(MODULE_CONTEXT, "api.log.requests", "false"));
    }

    public ApiRequestBuilder endpoint(String path)
    {
        this.endpoint=path;
        return this;
    }

    public ApiRequestBuilder header(String key, String value)
    {
        this.headers.put(key, value);
        return this;
    }

    public ApiRequestBuilder queryParam(String key, String value)
    {
        this.queryParams.put(key, value);
        return this;
    }

    public ApiRequestBuilder pathParam(String key, String value)
    {
        this.pathParams.put(key, value);
        return this;
    }

    public ApiRequestBuilder body(Object payload)
    {
        this.requestBody=payload;
        return this;
    }

    /**
     * Sets a Bearer token for auth. Optional - not required for current open endpoints,
     * but available for future endpoints that need it.
     */
    public ApiRequestBuilder auth(String token)
    {
        this.bearerToken=token;
        return this;
    }

    public ApiRequestBuilder log()
    {
        this.logEnabled=true;
        return this;
    }

    public Response get()
    {
        logger.info("Executing GET request - endpoint: {}", endpoint);
        Response response=buildRequestSpec().when().get(endpoint);
        logResponseSummary(response);
        return response;
    }

    public Response post()
    {
        logger.info("Executing POST request - endpoint: {}", endpoint);
        Response response=buildRequestSpec().when().post(endpoint);
        logResponseSummary(response);
        return response;
    }

    public Response put()
    {
        logger.info("Executing PUT request - endpoint: {}", endpoint);
        Response response=buildRequestSpec().when().put(endpoint);
        logResponseSummary(response);
        return response;
    }

    public Response delete()
    {
        logger.info("Executing DELETE request - endpoint: {}", endpoint);
        Response response=buildRequestSpec().when().delete(endpoint);
        logResponseSummary(response);
        return response;
    }

    /**
     * Assembles the RequestSpecification from all chained settings,
     * applying base URI, timeout, headers, params, body, and auth.
     */
    private RequestSpecification buildRequestSpec() {
        String baseUri=ConfigManager.getProperty(MODULE_CONTEXT, "api.base.url");
        int timeOutSeconds=Integer.parseInt(ConfigManager.getProperty(MODULE_CONTEXT, "api.timeout.seconds", "20"));

        logger.debug("Building request spec - baseUri: {}, timeOutSeconds: {}", baseUri, timeOutSeconds);

        RequestSpecification spec=RestAssured.given().baseUri(baseUri).contentType(ContentType.JSON)
                .config(RestAssured.config().httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeOutSeconds*1000)
                        .setParam("http.socket.timeout", timeOutSeconds*1000)));

        if (!headers.isEmpty())
        {
            logger.debug("Applying {} custom header(s)", headers.size());
            spec=spec.headers(headers);
        }

        if (!queryParams.isEmpty())
        {
            logger.debug("Applying {} query param(s)", queryParams.size());
            spec=spec.queryParams(queryParams);
        }

        if (!pathParams.isEmpty())
        {
            logger.debug("Applying {} path param(s)", pathParams.size());
            spec=spec.pathParams(pathParams);
        }

        if (bearerToken!=null && !bearerToken.trim().isEmpty())
        {
            logger.debug("Applying bearer token authentication");
            spec=spec.header("Authorization", "Bearer " + bearerToken);
        }

        if (requestBody!=null)
        {
            logger.debug("Attaching request body of type: {}", requestBody.getClass().getSimpleName());
            spec=spec.body(requestBody);
        }

        if (logEnabled)
        {
            spec=spec.log().all();
        }

        return spec;
    }

    private void logResponseSummary(Response response) {
        logger.info("Response received - status: {}, time: {}ms", response.getStatusCode(), response.getTime());
        if (response.getStatusCode()>=400)
        {
            logger.warn("Non-success status code received: {} for endpoint: {}", response.getStatusCode(), endpoint);
        }
    }

}
