package com.smartnote.server;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import com.smartnote.server.Server;
import com.smartnote.server.api.v1.Fetch;
import com.smartnote.server.auth.Session;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MIME;
import com.smartnote.testing.BaseRoute;

public class FetchTest extends BaseRoute {

    private Fetch fetch;
    public static final String TEST_FILE_NAME = "file.pdf";
    public static final String DATA;

    public static final String SUMMARY_FILE_LINES[] = {
        "# Summary",
        "## About",
        "This is some example markdown that will be exported"
    };

    static {
        StringBuilder builder = new StringBuilder();
        for (String line : SUMMARY_FILE_LINES) {
            builder.append(line);
            builder.append('\n');
        }
        DATA = builder.toString();
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        fetch = new Fetch();
        setRequestQueryParam("name", TEST_FILE_NAME);
        setRequestContentType(MIME.PDF);
        activateSession();
    }
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
    private Response doApiTest(int code, String mimeType) throws Exception {
        Response response = handle(fetch);
        int status = response.status();

        assertEquals(code, status);

        assertEquals(mimeType, response.type());
        assertTrue(responseJson().has("message"));
        return response;
    }
    /*
    @Test
    public void testHandleSuccess() throws Exception {
        // Set up any necessary request parameters or headers
        setRequestQueryParam("param", "value");

        // Handle the route
        Response result = handle(fetch);

        // Verify response code
        assertEquals(200, result.status());

        // Verify response type
        assertEquals("application/json", result.type());

        // Verify response body
        assertEquals("{\"message\": \"Expected message\"}", result.body());
    }
    */
    @Test
    public void testNoFileFound() throws Exception {
        // Set up any necessary request parameters or headers
        setRequestQueryParam("param", "value");

        // Handle the route
        Response result = handle(fetch);

        // Verify response code
        assertEquals(404, result.status());
        
        // Verify response body
        assertEquals("{\"message\":\"File not found\"}", result.body());
    }
    /*
    @Test
    public void testForbidden() throws Exception {
        // Set up any necessary request parameters or headers
        setRequestQueryParam("param1", "value1");

        // Handle the route
        Response result = handle(fetch);

        // Verify response code
        assertEquals(403, result.status());

        // Verify response body
        assertEquals("{\"message\":\"Access denied\"}", result.body());
    }
    @Test
    public void testNameNotSpecified() throws Exception {
        // Set up any necessary request parameters or headers
        setRequestQueryParam(null, "string");

        // Handle the route
        Response result = handle(fetch);

        // Verify response code
        assertEquals(400, result.status());

        // Verify response body
        assertEquals("{\"message\": \"Name was not specified\"}", result.body());
    }
    **/

}