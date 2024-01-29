package com.smartnote.testing;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.auth.SessionPermission;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.FileUtils;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Used for testing Spark routes.
 * 
 * @author Ethan Vrhel
 */
public class RouteTest extends BaseTest {
    public static final String SESSION_TOKEN = "test";

    private Request request;
    private Map<String, String> requestQueryParams;
    private String requestBody;
    private String requestContentType;
    private Session requestSession;

    private Map<String, String> responseHeaders;
    private String responseBody;
    private String responseType;
    private int responseStatus;

    public void setUp() throws Exception {
        super.setUp();

        setupServer();

        this.request = mockRequest();
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
    public Response handle(Route route) throws Exception {       
        Response response = mockResponse();

        Object r = route.handle(request, response);
        if (r != null)
            response.body(r.toString());
        
        if (response.status() == 0)
            response.status(200); // default status

        return response;
    }

    public Request request() {
        return request;
    }

    /**
     * Parse response body as JSON.
     * 
     * @return the JSON element.
     */
    public JsonObject responseJson() {
        return getGson().fromJson(responseBody, JsonObject.class);
    }

    public void setRequestQueryParam(String key, String value) {
        requestQueryParams.put(key, value);
    }

    public void removeRequestQueryParam(String key) {
        requestQueryParams.remove(key);
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public void startRequestSession() {
        this.requestSession = mockSession();
    }

    public void removeRequestSession() {
        this.requestSession = null;
    }

    private void setupServer() throws Exception {
        Server server = Server.getServer();
        Class<Server> serverClass = Server.class;

        Field resourceSystemField = serverClass.getDeclaredField("resourceSystem");
        resourceSystemField.setAccessible(true);
        resourceSystemField.set(server, createResourceSystem());

        Field sessionManagerField = serverClass.getDeclaredField("sessionManager");
        sessionManagerField.setAccessible(true);
        sessionManagerField.set(server, mockSessionManager());
    }

    private ResourceSystem createResourceSystem() throws Exception {
        ResourceConfig config = new ResourceConfig();
        ResourceSystem resourceSystem = new ResourceSystem(config);

        Class<ResourceSystem> resourceSystemClass = ResourceSystem.class;
        Field fileResourceFactoryField = resourceSystemClass.getDeclaredField("fileResourceFactory");
        fileResourceFactoryField.setAccessible(true);
        fileResourceFactoryField.set(resourceSystem, getFileSystem().createResourceFactory());

        return resourceSystem;
    }

    private SessionManager mockSessionManager() {
        SessionManager sessionManager = mock(SessionManager.class);

        when(sessionManager.getSession(any(Request.class))).thenAnswer(invokation -> requestSession);
        when(sessionManager.createSession()).thenAnswer(invokation -> requestSession);
        when(sessionManager.isTokenValid(anyString())).thenAnswer(invokation -> invokation.getArguments()[0].equals("test"));

        return sessionManager;
    }
    
    private Session mockSession() {
        Path sessionDirectory = Server.getServer().getResourceSystem().getSessionDir().resolve(SESSION_TOKEN);
        Path tokenFile = sessionDirectory.resolve(".token");

        SessionPermission permission = mock(SessionPermission.class);
        Session session = mock(Session.class);

        when(session.getId()).thenReturn(SESSION_TOKEN);
        when(session.getSessionDirectory()).thenReturn(sessionDirectory);

        doAnswer(invokation -> {
            Path path = (Path) invokation.getArguments()[0];
            path = sessionDirectory.resolve(path);
            if (!FileUtils.isPathInDirectory(path, sessionDirectory))
                throw new SecurityException("Access denied");
            return path;
        }).when(session).pathInSession(any(Path.class));

        doAnswer(invokation -> {
            Response response = (Response) invokation.getArguments()[0];
            response.header("Authorization", SESSION_TOKEN);
            return null;
        }).when(session).writeToResponse(any(Response.class));

        when(session.getPermission()).thenReturn(permission);
        when(permission.getSession()).thenReturn(session);

        return session;
    }

    private Request mockRequest() {
        Request request = mock(Request.class);

        doAnswer(invokation -> {
            return requestQueryParams.get(invokation.getArguments()[0]);
        }).when(request).queryParams(anyString());
        when(request.body()).thenAnswer(invokation -> requestBody);
        when(request.bodyAsBytes()).thenAnswer(invokation -> requestBody == null ? null : requestBody.getBytes());
        when(request.contentType()).thenAnswer(invokation -> requestContentType);
        
        this.requestQueryParams = new HashMap<>();
        return request;
    }

    private Response mockResponse() {
        responseHeaders = new HashMap<>();

        Response response = mock(Response.class);

        doAnswer(invokation -> {
            responseBody = (String) invokation.getArguments()[0];
            return null;
        }).when(response).body(anyString());

        doAnswer(invokation -> {
            responseType = (String) invokation.getArguments()[0];
            return null;
        }).when(response).type(anyString());

        doAnswer(invokation -> {
            responseStatus = (int) invokation.getArguments()[0];
            return null;
        }).when(response).status(anyInt());

        when(response.body()).thenAnswer(invokation -> responseBody);
        when(response.type()).thenAnswer(invokation -> responseType);
        when(response.status()).thenAnswer(invokation -> responseStatus);

        doAnswer(invokation -> {
            responseHeaders.put((String) invokation.getArguments()[0], (String) invokation.getArguments()[1]);
            return null;
        }).when(response).header(anyString(), anyString());

        return response;
    }
}
