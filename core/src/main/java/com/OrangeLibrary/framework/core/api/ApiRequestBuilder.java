package com.OrangeLibrary.framework.core.api;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

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
        return buildRequestSpec().when().get(endpoint);
    }

    public Response post()
    {
        return buildRequestSpec().when().post(endpoint);
    }

    public Response put()
    {
        return buildRequestSpec().when().put(endpoint);
    }

    public Response delete()
    {
        return buildRequestSpec().when().delete(endpoint);
    }

    /**
     * Assembles the RequestSpecification from all chained settings,
     * applying base URI, timeout, headers, params, body, and auth.
     */
    private RequestSpecification buildRequestSpec() {
        String baseUri=ConfigManager.getProperty(MODULE_CONTEXT, "api.base.url");
        int timeOutSeconds=Integer.parseInt(ConfigManager.getProperty(MODULE_CONTEXT, "api.timeout.seconds", "20"));

        RequestSpecification spec=RestAssured.given().baseUri(baseUri).contentType(ContentType.JSON)
                .config(RestAssured.config().httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeOutSeconds*1000)
                        .setParam("http.socket.timeout", timeOutSeconds*1000)));

        if (!headers.isEmpty())
        {
            spec=spec.headers(headers);
        }

        if (!queryParams.isEmpty())
        {
            spec=spec.queryParams(queryParams);
        }

        if (!pathParams.isEmpty())
        {
            spec=spec.pathParams(pathParams);
        }

        if (bearerToken!=null && !bearerToken.trim().isEmpty())
        {
            spec=spec.header("Authorization", "Bearer " + bearerToken);
        }

        if (requestBody!=null)
        {
            spec=spec.body(requestBody);
        }

        if (logEnabled)
        {
            spec=spec.log().all();
        }

        return spec;
    }

}
