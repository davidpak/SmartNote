package com.smartnote.testing;

import org.junit.After;
import org.junit.Before;

import com.google.gson.Gson;

/**
 * <p>Base class for tests. Stores a <code>VirtualFileSystem</code> and
 * a <code>Gson</code> instance.</p>
 * 
 * @author Ethan Vrhel
 * @see VirtualFileSystem
 */
public class BaseTest {
    private VirtualFileSystem vfs;
    //private SecurityManager securityManager;
    private Gson gson;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() throws Exception {
        this.vfs = new VirtualFileSystem();

        //this.securityManager = new TestingSecurityManager();
        //System.setSecurityManager(this.securityManager);

        this.gson = new Gson();
    }

    /**
     * Cleans up after the test.
     */
    @After
    public void tearDown() throws Exception {
        //System.setSecurityManager(null);
    }

    /**
     * Gets the VirtualFileSystem used for testing.
     * 
     * @return the VirtualFileSystem.
     */
    public VirtualFileSystem getFileSystem() {
        return vfs;
    }

    /**
     * Gets the Gson instance used for testing.
     * 
     * @return the Gson instance
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Asserts that System.exit() was called.
     * 
     * @param status the status code passed to System.exit()
     * @throws ExitException if System.exit() was not called.
     */
    public void assertExit(int status) {
        /*try {
            verify(securityManager).checkExit(status);
        } catch (ExitException e) {
            if (e.getStatus() != status)
                throw new AssertionError("Expected exit status " + status + ", got " + e.getStatus());
        }*/
    }

    /**
     * SecurityManager used for testing.
     */
    /*private class TestingSecurityManager extends SecurityManager {

        @Override
        public void checkPermission(Permission perm) {
            // allow everything
        }

        @Override
        public void checkExit(int status) {
            throw new ExitException(status);
        }

    }*/

}
