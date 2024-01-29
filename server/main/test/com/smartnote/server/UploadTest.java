package com.smartnote.server;

import static org.junit.Assert.*;

import org.junit.*;

import com.smartnote.server.api.v1.Upload;
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

    private Response doTest(int code) throws Exception {
        Response response = handle(upload);
        assertEquals(code, response.status());
        assertEquals("application/json", response.type());      
        assertTrue(responseJson().has("message"));
        return response;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        upload = new Upload();

        // basic setup, tests remove these to test specific cases
        setRequestQueryParam("name", TEST_FILE_NAME);
        setRequestBody(TEST_FILE_CONTENTS);
        setRequestContentType("application/pdf");
        startRequestSession();
    }

    @After
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
        removeRequestSession();
        doTest(401);
    }

    @Test
    public void testUploadBadName() throws Exception {
        setRequestQueryParam("name", "../../../badfile.pdf");
        doTest(403);
    }
}
