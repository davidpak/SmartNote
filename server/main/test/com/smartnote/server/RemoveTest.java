package com.smartnote.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.smartnote.server.api.v1.Remove;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.testing.BaseRoute;
import com.smartnote.testing.VirtualFileSystem;

/**
 * <p>Tests the <code>remove</code> RPC.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.api.v1.Remove
 */
public class RemoveTest extends BaseRoute {
    public static final String TEST_FILE = "test.txt";
    public static final byte[] TEST_FILE_DATA = "Hello World!".getBytes();

    private Remove remove;

    private Path publicPath;
    private Path privatePath;
    private Path sessionPath;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        remove = new Remove();

        ResourceConfig config = Server.getServer().getConfig().getResourceConfig();

        activateSession();

        publicPath = Paths.get(config.getPublicDir(), TEST_FILE);
        privatePath = Paths.get(config.getPrivateDir(), TEST_FILE);
        sessionPath = getSession(SESSION_TOKEN).getSessionDirectory().resolve(TEST_FILE);

        // write a file to all authorities
        writeTestFile(publicPath);
        writeTestFile(privatePath);
        writeTestFile(sessionPath);
    }

    @Test
    public void testRemoveSession() throws Exception {
        setRequestQueryParam("name", "session:" + TEST_FILE);
        doApiTest(remove, 200);
        assertFalse(getFileSystem().exists(sessionPath));
    }

    @Test
    public void testRemoveSessionUnauthorized() throws Exception {
        setRequestQueryParam("name", "session:" + TEST_FILE);
        deactivateSession();
        doApiTest(remove, 401);
        assertTrue(getFileSystem().exists(sessionPath));
    }

    @Test
    public void testRemovePublic() throws Exception {
        setRequestQueryParam("name", "public:" + TEST_FILE);
        doApiTest(remove, 403);
        assertTrue(getFileSystem().exists(publicPath));
    }

    @Test
    public void testRemovePublicUnauthorized() throws Exception {
        testRemovePublic(); // should be the same
    }

    @Test
    public void testRemovePrivate() throws Exception {
        setRequestQueryParam("name", "private:" + TEST_FILE);
        doApiTest(remove, 403);
        assertTrue(getFileSystem().exists(privatePath));
    }

    @Test
    public void testRemovePrivateUnauthorized() throws Exception {
        testRemovePrivate(); // should be the same
    }

    @Test
    public void testRemoveDoesntExist() throws Exception {
        setRequestQueryParam("name", "session:doesntexist");
        doApiTest(remove, 404);
    }

    @Test
    public void testRemoveInvalidName() throws Exception {
        setRequestQueryParam("name", "invalidname");
        doApiTest(remove, 400);
    }

    @Test
    public void testRemoveNoName() throws Exception {
        doApiTest(remove, 400);
    }

    private void writeTestFile(Path path) throws Exception {
        VirtualFileSystem fileSystem = getFileSystem();
        OutputStream out = fileSystem.openOutputStream(path);
        out.write(TEST_FILE_DATA);
        out.close();
    }
}
