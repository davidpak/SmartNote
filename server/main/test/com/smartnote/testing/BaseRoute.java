package com.smartnote.testing;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Used for testing Spark routes. This class creates mock <code>Request</code>
 * and <code>Response</code> instances and provides methods for setting the
 * request parameters and handling the route.</p>
 * 
 * <p>Not all methods are implemented. If you need to use a method that is not
 * implemented, add it to the class and submit a pull request.</p>
 * 
 * @author Ethan Vrhel
 * @see spark.Route
 * @see spark.Request
 * @see spark.Response
 */
public class BaseRoute extends BaseServer {
    
    // request
    private Request request;
    private Map<String, String> requestQueryParams;
    private Map<String, String> requestHeaders;
    private Map<String, String> requestCookies;
    private String requestBody;
    private String requestContentType;

    // response
    private Map<String, String> responseHeaders;
    private Map<String, AbstractMap.SimpleEntry<String, Integer>> responseCookies;
    private String responseBody;
    private String responseType;
    private int responseStatus;

    /**
     * Sets up the framework for testing routes.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.request = mockRequest();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
  
    /**
     * Handle a route.
     * 
     * @param route the route to handle.
     * @throws Exception if an error occurs.
     */
    public Response handle(Route route) throws Exception {
        Response response = mockResponse();

        Object r = route.handle(request, response);
        if (r != null)
            response.body(r.toString());
        
        if (response.status() == 0)
            response.status(200); // default status

        return response;
    }

    /**
     * Tests basic functionality of an RPC. This includes checking the response
     * code, the response type, and the response body.
     * 
     * @param route the route to test.
     * @param code the expected response code.
     * 
     * @return the response.
     */
    public Response doApiTest(Route route, int code) throws Exception {
        Response response = handle(route);
        int status = response.status();

        assertEquals(code, status);

        assertEquals("application/json", response.type());
        assertTrue(responseJson().has("message"));

        return response;
    }

    /**
     * Parse response body as JSON.
     * 
     * @return the JSON element.
     */
    public JsonObject responseJson() {
        return getGson().fromJson(responseBody, JsonObject.class);
    }

    public Session responseSession() {
        var p = responseCookies.get(SessionManager.COOKIE_NAME);
        if (p == null) return null;
        return getSession(p.getKey());
    }

    /**
     * Sets a query parameter for the request.
     * 
     * @param key the key.
     * @param value the value.
     */
    public void setRequestQueryParam(String key, String value) {
        requestQueryParams.put(key, value);
    }

    public String getRequestQueryParam(String key) {
        return requestQueryParams.get(key);
    }

    /**
     * Removes a query parameter from the request.
     * 
     * @param key the key.
     */
    public void removeRequestQueryParam(String key) {
        requestQueryParams.remove(key);
    }

    public void setRequestCookie(String key, String value) {
        requestCookies.put(key, value);
    }

    public void removeRequestCookie(String key) {
        requestCookies.remove(key);
    }

    /**
     * Sets the request body.
     * 
     * @param requestBody the request body.
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Sets the request content type.
     * 
     * @param requestContentType the request content type.
     */
    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public void activateSession() {
        requestCookies.put(SessionManager.COOKIE_NAME, SESSION_TOKEN);
    }

    public void deactivateSession() {
        requestCookies.remove(SessionManager.COOKIE_NAME);
    }
    
    // creates a mock request
    private Request mockRequest() {
        Request request = mock(Request.class);

        // Request.queryParams(String)
        doAnswer(invokation -> {
            return requestQueryParams.get(invokation.getArguments()[0]);
        }).when(request).queryParams(anyString());

        // Request.headers(String)
        doAnswer(invokation -> {
            return requestHeaders.get(invokation.getArguments()[0]);
        }).when(request).headers(anyString());

        // Request.cookie(String)
        doAnswer(invokation -> {
            return requestCookies.get(invokation.getArguments()[0]);
        }).when(request).cookie(anyString());

        // Request.body()
        when(request.body()).thenAnswer(invokation -> requestBody);

        // Request.bodyAsBytes()
        when(request.bodyAsBytes()).thenAnswer(invokation -> requestBody == null ? null : requestBody.getBytes());

        // Request.contentType()
        when(request.contentType()).thenAnswer(invokation -> requestContentType);
        
        this.requestQueryParams = new HashMap<>();
        this.requestHeaders = new HashMap<>();
        this.requestCookies = new HashMap<>();
        return request;
    }

    // creates a mock response
    private Response mockResponse() {
        responseHeaders = new HashMap<>();
        responseCookies = new HashMap<>();

        Response response = mock(Response.class);

        // Response.body(String)
        doAnswer(invokation -> {
            responseBody = (String) invokation.getArguments()[0];
            return null;
        }).when(response).body(anyString());

        // Response.type(String)
        doAnswer(invokation -> {
            responseType = (String) invokation.getArguments()[0];
            return null;
        }).when(response).type(anyString());

        // Response.status(int)
        doAnswer(invokation -> {
            responseStatus = (int) invokation.getArguments()[0];
            return null;
        }).when(response).status(anyInt());

        // Response.body()
        when(response.body()).thenAnswer(invokation -> responseBody);

        // Response.type()
        when(response.type()).thenAnswer(invokation -> responseType);

        // Response.status()
        when(response.status()).thenAnswer(invokation -> responseStatus);

        // Response.header(String, String)
        doAnswer(invokation -> {
            responseHeaders.put((String) invokation.getArguments()[0], (String) invokation.getArguments()[1]);
            return null;
        }).when(response).header(anyString(), anyString());

        // Response.cookie(String, String, int)
        doAnswer(invokation -> {
            responseCookies.put((String) invokation.getArguments()[0], new AbstractMap.SimpleEntry<>((String) invokation.getArguments()[1], (int) invokation.getArguments()[2]));
            return null;
        }).when(response).cookie(anyString(), anyString(), anyInt());
        
        // Response.cookie(String, String)
        doAnswer(invokation -> {
            responseCookies.put((String) invokation.getArguments()[0], new AbstractMap.SimpleEntry<>((String) invokation.getArguments()[1], null));
            return null;
        }).when(response).cookie(anyString(), anyString());

        return response;
    }
}
