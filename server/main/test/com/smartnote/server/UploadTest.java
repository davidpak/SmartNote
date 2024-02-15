package com.smartnote.server;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.*;

import com.smartnote.server.api.v1.Upload;
import com.smartnote.server.auth.Session;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.util.MIME;
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
    public static final byte[] TEST_FILE_CONTENTS;

    static {
        Path[] paths = {
            Paths.get("server", "testfiles", TEST_FILE_NAME),
            Paths.get("testfiles", TEST_FILE_NAME)
        };

        byte[] contents = null;
        for (int i = 0; i < paths.length; i++) {
            Path path = paths[i];
            if (Files.exists(path)) {
                try {
                    contents = Files.readAllBytes(path);
                    break;
                } catch (Exception e) {}
            }
        }

        if (contents == null)
            throw new RuntimeException("Could not find test file");

        TEST_FILE_CONTENTS = contents;
    }
    
    private Upload upload;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        upload = new Upload();

        // basic setup, tests remove these to test specific cases
        setRequestQueryParam("name", TEST_FILE_NAME);
        setRequestBody(TEST_FILE_CONTENTS);
        setRequestContentType(MIME.PDF);
        activateSession();
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private Response doApiTest(int code) throws Exception {
        Response response = doApiTest(upload, code);
        int status = response.status();

        // some tests use a different file name
        String fileName = getRequestQueryParam("name");
        if (fileName == null)
            fileName = TEST_FILE_NAME; // guess

        if (status == 200) {
            // if response is OK, check that the file was uploaded
            Session session = responseSession();
            assertNotNull(session);

            Path uploadPath = session.pathInSession(Paths.get("uploads", fileName));
            assertTrue(getFileSystem().exists(uploadPath));
        } else {
            // otherwise, check that the file was not uploaded
            assertFalse(getFileSystem().containsFileWithName(Paths.get(fileName)));
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
        setRequestBody((byte[]) null);
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

    @Test
    public void testUploadTooLarge() throws Exception {
        ResourceConfig config = Server.getServer().getConfig().getResourceConfig();
        Class<ResourceConfig> configClass = ResourceConfig.class;
        Field maxUploadSizeField = configClass.getDeclaredField("maxUploadSize");
        maxUploadSizeField.setAccessible(true);
        maxUploadSizeField.setLong(config, 1); // 1 byte max upload size

        doApiTest(413);
    }

    @Test
    public void testQuotaExceeded() throws Exception {
        ResourceConfig config = Server.getServer().getConfig().getResourceConfig();
        Class<ResourceConfig> configClass = ResourceConfig.class;
        Field sessionQuotaField = configClass.getDeclaredField("sessionQuota");
        sessionQuotaField.setAccessible(true);
        sessionQuotaField.setLong(config, 1); // 1 byte session quota
        // leave max upload size at original value

        doApiTest(413);
    }

    @Test
    public void testUploadBadContentType() throws Exception {
        // TODO: add this back, we allowed plain for now
        //setRequestContentType("text/plain");
        //doApiTest(406);
    }

    @Test
    public void testInferContentType() throws Exception {
        setRequestContentType(null);
        doApiTest(200);
    }

    @Test
    public void testInferBadContentType() throws Exception {
        // TODO: add this back, we allowed plain for now
        //setRequestContentType(null);
        //setRequestQueryParam("name", "file.bad");
        //doApiTest(406);
    }
}
