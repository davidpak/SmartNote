package com.smartnote.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.smartnote.server.auth.Session;

/**
 * Interface for accessing resources.
 * 
 * @author Ethan Vrhel
 */
public class Resource {
    /**
     * The private directory.
     */
    public static final String PRIVATE_DIR = "private";

    /**
     * The public directory.
     */
    public static final String PUBLIC_DIR = "public";

    /**
     * Per-session directory.
     */
    public static final String SESSION_DIR = "sessions";

    static {
        // delete all files in session directory
        File[] files = new File(SESSION_DIR).listFiles();
        if (files != null) {
            for (File f : files)
                f.delete();
        }
    }

    /**
     * Open an output stream to a private resource.
     * 
     * @param name The name of the resource.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the private
     *                                directory.
     */
    public static InputStream readPrivate(String name) throws IOException, IllegalAccessException {
        String path = PRIVATE_DIR + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        // check if file is in private directory
        if (!f.getAbsolutePath().startsWith(new File(PRIVATE_DIR).getAbsolutePath()))
            throw new IllegalAccessException(path + " is not in the private directory");

        return new FileInputStream(f);
    }

    /**
     * Opens an output stream to a private resource.
     * 
     * @param name The name of the resource.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the private
     *                                directory.
     */
    public static OutputStream writePrivate(String name) throws IOException, IllegalAccessException {
        String path = PRIVATE_DIR + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        // check if file is in private directory
        if (!f.getAbsolutePath().startsWith(new File(PRIVATE_DIR).getAbsolutePath()))
            throw new IllegalAccessException(path + " is not in the private directory");

        return new FileOutputStream(f);
    }

    /**
     * Opens an output stream to a private resource.
     * 
     * @param name  The name of the resource.
     * @param bytes The bytes to write.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the private
     *                                directory.
     */
    public static OutputStream writePrivate(String name, byte[] bytes) throws IOException, IllegalAccessException {
        OutputStream out = writePrivate(name);
        out.write(bytes);
        out.close();
        return out;
    }

    /**
     * Open an input stream to a public resource.
     * 
     * @param name The name of the resource.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IllegalAccessException If the resource is not in the public
     *                                directory.
     * @throws IOException            If an I/O error occurs.
     */
    public static InputStream readPublic(String name) throws IOException, IllegalAccessException {
        String path = PUBLIC_DIR + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        // check if file is in public directory
        if (!f.getAbsolutePath().startsWith(new File(PUBLIC_DIR).getAbsolutePath()))
            throw new IllegalAccessException(path + " is not in the public directory");

        return new FileInputStream(f);
    }

    /**
     * Opens an input stream to a session resource.
     * 
     * @param name  The name of the resource.
     * @param token The session token.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the session
     *                                directory or the session token is invalid.
     */
    public static InputStream readSession(String name, String token)
            throws IOException, IllegalAccessException {
        if (!Session.isTokenValid(token))
            throw new IllegalAccessException("Invalid session token");

        String path = SESSION_DIR + File.separatorChar + token + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        // session directory
        String dir = new File(SESSION_DIR + File.separatorChar + token).getAbsolutePath();

        // check if file is in session directory for the session
        if (!f.getAbsolutePath().startsWith(dir))
            throw new IllegalAccessException(path + " is not in the session directory for the session");

        return new FileInputStream(f);
    }

    /**
     * Opens an output stream to a private resource.
     * 
     * @param name The name of the resource.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the private
     *                                directory.
     */
    public static OutputStream writeSession(String name, String token)
            throws IOException, IllegalAccessException {
        if (!Session.isTokenValid(token))
            throw new IllegalAccessException("Invalid session token");

        String path = SESSION_DIR + File.separatorChar + token + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        // session directory
        String dir = new File(SESSION_DIR + File.separatorChar + token).getAbsolutePath();

        // check if file is in session directory for the session
        if (!f.getAbsolutePath().startsWith(dir))
            throw new IllegalAccessException(path + " is not in the session directory for the session");

        return new FileOutputStream(f);
    }

    // prevent instantiation
    private Resource() {
    }
}
