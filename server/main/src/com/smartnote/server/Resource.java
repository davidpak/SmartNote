package com.smartnote.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.smartnote.server.auth.Session;
import com.smartnote.server.util.FileUtils;

/**
 * <p>Interface for accessing resources. The actual location of
 * resources is not specified, as this serves as an abstraction
 * layer for accessing resources.</p>
 * 
 * <p>Some resources are protected and may only be accessed by
 * a valid session.</p>
 * 
 * @author Ethan Vrhel
 * @see {@link Session}
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

    public static final File PRIVATE_FILE = new File(PRIVATE_DIR).getAbsoluteFile();
    public static final File PUBLIC_FILE = new File(PUBLIC_DIR).getAbsoluteFile();
    public static final File SESSION_FILE = new File(SESSION_DIR).getAbsoluteFile();

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
        if (!FileUtils.isFileInDirectory(f, new File(PRIVATE_DIR)))
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
        if (!FileUtils.isFileInDirectory(f, PRIVATE_FILE))
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
        if (!FileUtils.isFileInDirectory(f, PUBLIC_FILE))
            throw new IllegalAccessException(path + " is not in the public directory");

        return new FileInputStream(f);
    }

    /**
     * Opens an input stream to a session resource.
     * 
     * @param name  The name of the resource.
     * @param session The session.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the session
     *                                directory or the session token is invalid.
     */
    public static InputStream readSession(String name, Session session)
            throws IOException, IllegalAccessException {
        String sessionDirName = session.getJWT().getSubject();

        String path = SESSION_DIR + File.separatorChar + sessionDirName + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        // session directory
        String dir = new File(SESSION_DIR + File.separatorChar + sessionDirName).getAbsolutePath();

        // check if file is in session directory for the session
        if (!FileUtils.isFileInDirectory(f, new File(dir)))
            throw new IllegalAccessException(path + " is not in the session directory for the session");

        return new FileInputStream(f);
    }

    /**
     * Opens an output stream to a private resource.
     * 
     * @param name  The name of the resource.
     * @param session The session.
     * @return The stream to the resource, or <code>null</code> if
     *         the resource does not exist.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not in the private
     *                                directory.
     */
    public static OutputStream writeSession(String name, Session session)
            throws IOException, IllegalAccessException {
        String sessionDirName = session.getJWT().getSubject();

        String path = SESSION_DIR + File.separatorChar + sessionDirName + File.separatorChar + name;

        File f = new File(path).getAbsoluteFile();
        File sessionDir = new File(SESSION_DIR + File.separatorChar + sessionDirName);

        // check if file is in session directory for the session
        if (!FileUtils.isFileInDirectory(f, sessionDir))
            throw new IllegalAccessException(path + " is not in the session directory for the session");

        // create directory if it doesn't exist
        File parent = f.getParentFile();
        if (!parent.exists())
            parent.mkdirs();

        return new FileOutputStream(f);
    }

    // prevent instantiation
    private Resource() {}
}
