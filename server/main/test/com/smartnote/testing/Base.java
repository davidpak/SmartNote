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
public class Base {
    private VirtualFileSystem vfs;
    private Gson gson;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() throws Exception {
        this.vfs = new VirtualFileSystem();
        this.gson = new Gson();
    }

    /**
     * Cleans up after the test.
     */
    @After
    public void tearDown() throws Exception {
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
}
