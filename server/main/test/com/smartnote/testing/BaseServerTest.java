package com.smartnote.testing;

import static org.mockito.Mockito.*;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;

import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.auth.SessionPermission;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.CryptoUtils;
import com.smartnote.server.util.FileUtils;

import spark.Request;
import spark.Response;

/**
 * <p>Base class for tests that require a running the server (i.e. ones
 * that directly or indirectly use the <code>Server</code> class).
 * Provides a <code>Session</code> instance that can be used to
 * test components that require a session.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Server
 */
public class BaseServerTest extends BaseTest {
    public static final String SESSION_TOKEN = "test";

    @Override
    public void setUp() throws Exception {
        super.setUp();

        CryptoUtils.init(null);

        setupServer();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public Session getSession(String authToken) {
        if (authToken == null)
            return null;
        if (!authToken.equals(SESSION_TOKEN))
            return null;
        return createSessionObject();
    }
    
    // sets up the server for testing
    private void setupServer() throws Exception {
        Server server = Server.getServer();
        Class<Server> serverClass = Server.class;

        // set the resource system
        Field resourceSystemField = serverClass.getDeclaredField("resourceSystem");
        resourceSystemField.setAccessible(true);
        resourceSystemField.set(server, createResourceSystem());

        // set the session manager
        Field sessionManagerField = serverClass.getDeclaredField("sessionManager");
        sessionManagerField.setAccessible(true);
        sessionManagerField.set(server, createSessionManager());
    }

    // creates the resource system for testing
    private ResourceSystem createResourceSystem() throws Exception {
        ResourceConfig config = new ResourceConfig();
        ResourceSystem resourceSystem = new ResourceSystem(config);

        Class<ResourceSystem> resourceSystemClass = ResourceSystem.class;
        Field fileResourceFactoryField = resourceSystemClass.getDeclaredField("fileResourceFactory");
        fileResourceFactoryField.setAccessible(true);
        fileResourceFactoryField.set(resourceSystem, getFileSystem().createResourceFactory());

        return resourceSystem;
    }

    // creates the session manager for testing
    private SessionManager createSessionManager() {
        // always return our mock session
        SessionManager sessionManager = mock(SessionManager.class);

        when(sessionManager.getSession(any(Request.class))).thenAnswer(invokation -> {
            Request request = (Request) invokation.getArguments()[0];
            return getSession(request.headers("Authorization"));
        });

        when(sessionManager.createSession()).thenAnswer(invokation -> createNewSession());

        when(sessionManager.isTokenValid(anyString())).thenAnswer(invokation -> invokation.getArguments()[0].equals(SESSION_TOKEN));
        
        return sessionManager;
    }

    private Session createNewSession() throws Exception {
        Session session = createSessionObject();
        
        // create the token file
        Path tokenFile = session.getSessionDirectory().resolve(".token");
        OutputStream tokenOut = getFileSystem().openOutputStream(tokenFile);
        tokenOut.write(SESSION_TOKEN.getBytes());
        tokenOut.close();

        return session;
    }

    private Session createSessionObject() {
        Session session = mock(Session.class);
        SessionPermission permission = mock(SessionPermission.class);

        Path sessionDirectory = Server.getServer().getResourceSystem().getSessionDir().resolve(SESSION_TOKEN);
        
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
}