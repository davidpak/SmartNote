package com.smartnote.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.testing.BaseServer;

public class SessionTest extends BaseServer {
    public static final String TOKEN_SUBJECT = "subject";

    private Session session;
    private DecodedJWT jwt;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();

        jwt = mock(DecodedJWT.class);
        when(jwt.getSubject()).thenReturn(TOKEN_SUBJECT);
        when(jwt.getIssuer()).thenReturn(SessionManager.ISSUER);

        Class<Session> sessionClass = Session.class;
        Constructor<Session> sessionConstructor = sessionClass.getDeclaredConstructor(DecodedJWT.class);
        sessionConstructor.setAccessible(true);
        session = sessionConstructor.newInstance(jwt);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
