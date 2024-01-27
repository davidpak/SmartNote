package com.smartnote.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import com.google.gson.JsonElement;
import com.smartnote.server.api.v1.Upload;
import com.smartnote.testing.RouteTest;

public class UploadTest extends RouteTest {
    public static final String TEST_FILE_NAME = "file.txt";
    public static final String TEST_FILE_CONTENTS = "Hello, world!";
    
    private Upload upload;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        upload = new Upload();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testUploadBasic() throws Exception {
        when(request.queryParams("name")).thenReturn(TEST_FILE_NAME);
        when(request.body()).thenReturn(TEST_FILE_CONTENTS);

        handle(upload);

        assertEquals(200, response.status());
        assertEquals("application/json", response.type());
        
        JsonElement element = responseJson();
        assertTrue(element.isJsonObject());
        assertTrue(element.getAsJsonObject().has("message"));
        // don't care about message contents
    }
}
