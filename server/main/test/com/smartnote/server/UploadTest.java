package com.smartnote.server;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.*;

import com.smartnote.server.api.v1.Upload;
import com.smartnote.server.auth.Session;
import com.smartnote.testing.BaseRoute;

import spark.Response;

/**
 * <p>Tests the Upload RPC.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.api.v1.Upload
 */
public class UploadTest extends BaseRoute {
    public static final String TEST_FILE_NAME = "file.pdf";
    public static final String TEST_FILE_CONTENTS = "Hello, world!";
    
    private Upload upload;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        upload = new Upload();

        // basic setup, tests remove these to test specific cases
        setRequestQueryParam("name", TEST_FILE_NAME);
        setRequestBody(TEST_FILE_CONTENTS);
        setRequestContentType("application/pdf");
        activateSession();
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private Response doApiTest(int code) throws Exception {
        Response response = doApiTest(upload, code);
        int status = response.status();

        // if response is OK, check that the file was uploaded
        if (status == 200) {
            Session session = responseSession();
            assertNotNull(session);

            Path uploadPath = session.pathInSession(Paths.get("uploads", TEST_FILE_NAME));
            assertTrue(getFileSystem().exists(uploadPath));
        }

        return response;
    }

    @Test
    public void testUploadBasic() throws Exception {
        doApiTest(200);
    }

    @Test
    public void testUploadNoName() throws Exception {
        removeRequestQueryParam("name");
        doApiTest(400);
    }

    @Test
    public void testUploadNoBody() throws Exception {
        setRequestBody(null);
        doApiTest(400);
    }

    @Test
    public void testUploadNoSession() throws Exception {
        deactivateSession();
        doApiTest(401);
    }

    @Test
    public void testUploadBadName() throws Exception {
        setRequestQueryParam("name", "../../../badfile.pdf");
        doApiTest(403);
    }
}
