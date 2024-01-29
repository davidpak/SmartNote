package com.smartnote.server;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.*;

import com.smartnote.server.api.v1.Upload;
import com.smartnote.server.auth.Session;
import com.smartnote.testing.RouteTest;

import spark.Response;

/**
 * <p>Tests the Upload RPC.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.api.v1.Upload
 */
public class UploadTest extends RouteTest {
    public static final String TEST_FILE_NAME = "file.pdf";
    public static final String TEST_FILE_CONTENTS = "Hello, world!";
    
    private Upload upload;

    /**
     * Perform the test on Upload. Tests that the response code is the expected
     * code and that the response is in JSON format. If the response code is 200,
     * the test also checks that the file was uploaded.
     * 
     * @param code the expected response code.
     * @return the response.
     * @throws Exception if an error occurs.
     */
    private Response doTest(int code) throws Exception {
        Response response = handle(upload);
        int status = response.status();

        assertEquals(code, status);

        assertEquals("application/json", response.type());
        assertTrue(responseJson().has("message"));

        // if response is OK, check that the file was uploaded
        if (status == 200) {
            Session session = getSession();
            Path uploadPath = session.pathInSession(Paths.get("uploads", TEST_FILE_NAME));
            assertTrue(getFileSystem().exists(uploadPath));
        }

        return response;
    }

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

    @Test
    public void testUploadBasic() throws Exception {
        doTest(200);
    }

    @Test
    public void testUploadNoName() throws Exception {
        removeRequestQueryParam("name");
        doTest(400);
    }

    @Test
    public void testUploadNoBody() throws Exception {
        setRequestBody(null);
        doTest(400);
    }

    @Test
    public void testUploadNoSession() throws Exception {
        deactivateSession();
        doTest(401);
    }

    @Test
    public void testUploadBadName() throws Exception {
        setRequestQueryParam("name", "../../../badfile.pdf");
        doTest(403);
    }
}
