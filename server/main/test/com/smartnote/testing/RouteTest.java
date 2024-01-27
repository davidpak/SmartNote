package com.smartnote.testing;

import static org.mockito.Mockito.*;

import com.google.gson.JsonElement;
import com.smartnote.server.auth.Session;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Used for testing Spark routes.
 * 
 * @author Ethan Vrhel
 */
public class RouteTest extends BaseTest {
    protected Request request;

    protected Response response;
    private String responseBody;
    private int responseStatus;

    protected Session session;

    public void setUp() throws Exception {
        super.setUp();
        
        // mock request
        request = mock(Request.class);

        // mock response
        response = mock(Response.class);
        doAnswer(invokation -> {
            responseBody = (String) invokation.getArguments()[0];
            return null;
        }).when(response).body(anyString());
        doAnswer(invokation -> {
            responseStatus = (int) invokation.getArguments()[0];
            return null;
        }).when(response).status(anyInt());
        when(response.body()).thenAnswer(invokation -> responseBody);
        when(response.status()).thenAnswer(invokation -> responseStatus);

        // mock session
        session = mock(Session.class);
        when(session.getId()).thenReturn("test");

        //when(Session.getSession(request)).thenReturn(session);
        //when(Session.createSession()).thenReturn(session);
        //when(Session.isTokenValid(anyString())).thenReturn(true);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
  
    /**
     * Handle a route.
     * 
     * @param route the route to handle.
     * @throws Exception if an error occurs.
     */
    public void handle(Route route) throws Exception {
        Object r = route.handle(request, response);
        if (r != null)
            response.body(r.toString());
        
        if (response.status() == 0)
            response.status(200); // default status
    }

    /**
     * Parse response body as JSON.
     * 
     * @return the JSON element.
     */
    public JsonElement responseJson() {
        return getGson().toJsonTree(response.body());
    }
}
