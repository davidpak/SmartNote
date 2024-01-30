package com.smartnote.server;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.*;

import com.smartnote.server.api.v1.Login;
import com.smartnote.server.auth.Session;
import com.smartnote.testing.RouteTest;

import spark.Response;

public class LoginTest extends RouteTest {

    private Login login;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        login = new Login();
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private Response doApiTest(int code) throws Exception {
        Response response = doApiTest(login, code);
        int status = response.status();

        if (status == 200) {
            Session session = responseSession();
            assertNotNull(session);

            Path tokenPath = session.pathInSession(Paths.get(".token"));
            assertTrue(getFileSystem().exists(tokenPath));
        }

        return response;
    }

    @Test
    public void testLoginBasic() throws Exception {
        doApiTest(200);
    }

    @Test
    public void testLoginAlreadyAuthenticated() throws Exception {
        activateSession();
        doApiTest(400);
    }
}
