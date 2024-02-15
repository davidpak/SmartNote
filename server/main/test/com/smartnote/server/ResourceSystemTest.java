package com.smartnote.server;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.AllPermission;
import java.security.Permission;

import org.junit.Test;

import com.smartnote.server.auth.Session;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MIME;
import com.smartnote.testing.BaseServer;
import com.smartnote.testing.VirtualFileSystem;

public class ResourceSystemTest extends BaseServer {
    public static final String FILE_NAME = "test.txt";
    public static final String FILE_CONTENT = "Hello, world!";
    public static final String WRITE_CONTENT = "Goodbye, world!";

    private ResourceSystem resourceSystem;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        resourceSystem = Server.getServer().getResourceSystem();

        VirtualFileSystem vfs = getFileSystem();
        Session session = getSession(SESSION_TOKEN);

        OutputStream out;

        // create a file in each directory

        out = vfs.openOutputStream(resourceSystem.getPublicDir().resolve(FILE_NAME));
        out.write(FILE_CONTENT.getBytes());
        out.close();

        out = vfs.openOutputStream(resourceSystem.getPrivateDir().resolve(FILE_NAME));
        out.write(FILE_CONTENT.getBytes());
        out.close();

        out = vfs.openOutputStream(session.getSessionDirectory().resolve(FILE_NAME));
        out.write(FILE_CONTENT.getBytes());
        out.close();
    }

    private void testRead(String auth, Permission permission) throws Exception {
        Resource resource = resourceSystem.findResource(auth + ":" + FILE_NAME, permission);

        InputStream in = resource.openInputStream();
        byte[] buffer = new byte[FILE_CONTENT.length()];
        in.read(buffer);
        in.close();
    
        assertEquals(FILE_CONTENT, new String(buffer));
    }

    private void testWrite(String auth, Permission permission) throws Exception {
        Resource resource = resourceSystem.findResource(auth + ":" + FILE_NAME, permission);
        OutputStream out = resource.openOutputStream();
        out.write(WRITE_CONTENT.getBytes());
        out.close();

        InputStream in = resource.openInputStream();
        byte[] buffer = new byte[WRITE_CONTENT.length()];
        in.read(buffer);
        in.close();

        assertEquals(WRITE_CONTENT, new String(buffer));
    }
    
    private void testPublicRead(Permission permission) throws Exception {
        testRead("public", permission);
    }

    private void testPublicWrite(Permission permission) throws Exception {
        testWrite("public", permission);
    }
    
    @Test
    public void testReadPublicDefaultPermission() throws Exception {
        testPublicRead(null);
    }

    @Test(expected = SecurityException.class)
    public void testWritePublicDefaultPermission() throws Exception {
        testPublicWrite(null);
    }

    @Test
    public void testReadPublicSessionPermission() throws Exception {
        testPublicRead(getSession(SESSION_TOKEN).getPermission());
    }

    @Test(expected = SecurityException.class)
    public void testWriteSessionPermission() throws Exception {
        testPublicWrite(getSession(SESSION_TOKEN).getPermission());
    }

    @Test
    public void testReadPublicAllPermission() throws Exception {
        testPublicRead(new AllPermission());
    }

    @Test(expected = SecurityException.class)
    public void testWritePublicAllPermission() throws Exception {
        testPublicWrite(new AllPermission());
    }

    @Test(expected = SecurityException.class)
    public void testReadSessionDefaultPermission() throws Exception {
        testRead("session", null);
    }

    @Test(expected = SecurityException.class)
    public void testWriteSessionDefaultPermission() throws Exception {
        testWrite("session", null);
    }

    @Test
    public void testReadSessionSessionPermission() throws Exception {
        testRead("session", getSession(SESSION_TOKEN).getPermission());
    }

    @Test
    public void testWriteSessionSessionPermission() throws Exception {
        testWrite("session", getSession(SESSION_TOKEN).getPermission());
    }

    @Test(expected = SecurityException.class)
    public void testReadSessionAllPermission() throws Exception {
        testRead("session", new AllPermission());
    }

    @Test(expected = SecurityException.class)
    public void testWriteSessionAllPermission() throws Exception {
        testWrite("session", new AllPermission());
    }

    @Test(expected = SecurityException.class)
    public void testReadPrivateDefaultPermission() throws Exception {
        testRead("private", null);
    }

    @Test(expected = SecurityException.class)
    public void testWritePrivateDefaultPermission() throws Exception {
        testWrite("private", null);
    }

    @Test(expected = SecurityException.class)
    public void testReadPrivateSessionPermission() throws Exception {
        testRead("private", getSession(SESSION_TOKEN).getPermission());
    }

    @Test(expected = SecurityException.class)
    public void testWritePrivateSessionPermission() throws Exception {
        testWrite("private", getSession(SESSION_TOKEN).getPermission());
    }

    @Test
    public void testReadPrivateAllPermission() throws Exception {
        testRead("private", new AllPermission());
    }

    @Test(expected = SecurityException.class)
    public void testWritePrivateAllPermission() throws Exception {
        testWrite("private", new AllPermission());
    }

    @Test(expected = InvalidPathException.class)
    public void testPathMissingAuthority() throws Exception {
        resourceSystem.findResource("invalid", null);
    }

    @Test(expected = InvalidPathException.class)
    public void testPathStartsWithDot() throws Exception {
        resourceSystem.findResource(".invalid", null);
    }

    @Test(expected = InvalidPathException.class)
    public void testPathStartsWithDot2() throws Exception {
        resourceSystem.findResource("first/.invalid", null);
    }

    @Test(expected = NoSuchResourceException.class)
    public void testInvalidAuthority() throws Exception {
        resourceSystem.findResource("invalid:invalid", null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullAuthority() throws Exception {
        resourceSystem.findResource(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testActualNullAuthority() throws Exception {
        resourceSystem.findActualResource(null, Paths.get(FILE_NAME), null);
    }

    @Test(expected = NullPointerException.class)
    public void testActualNullPath() throws Exception {
        resourceSystem.findActualResource("public", null, null);
    }

    @Test
    public void testSupportsMIMETypes() {
        assertTrue(ResourceSystem.isSupportedType(MIME.PDF));
        assertTrue(ResourceSystem.isSupportedType(MIME.PPTX));
        assertTrue(ResourceSystem.isSupportedType(MIME.PPT));

        // some random types
        assertFalse(ResourceSystem.isSupportedType(MIME.JPEG));
        assertFalse(ResourceSystem.isSupportedType(MIME.MP3));
        // TODO: add this back in
        //assertFalse(ResourceSystem.isSupportedType(MIME.TEXT));
        assertFalse(ResourceSystem.isSupportedType(MIME.ZIP));
    }

    @Test
    public void testGetActualPath() {
        String path;

        path = resourceSystem.getActualPath("public:" + FILE_NAME);
        assertEquals("public:" + FILE_NAME, path);

        path = resourceSystem.getActualPath("public:./" + FILE_NAME);
        assertEquals("public:" + FILE_NAME, path);

        path = resourceSystem.getActualPath("public:somedir/" + FILE_NAME);
        assertEquals("public:somedir/" + FILE_NAME, path);

        path = resourceSystem.getActualPath("public:somedir/../" + FILE_NAME);
        assertEquals("public:" + FILE_NAME, path);
    }

    @Test(expected = InvalidPathException.class)
    public void testGetActualOutsideAuthority() {
        resourceSystem.getActualPath("public:../" + FILE_NAME);
    }

    @Test(expected = InvalidPathException.class)
    public void testGetActualNoAuthority() {
        resourceSystem.getActualPath(FILE_NAME);
    }
}
