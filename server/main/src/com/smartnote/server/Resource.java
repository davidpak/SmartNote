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
 * <p>
 * Interface for accessing resources. The actual location of
 * resources is not specified, as this serves as an abstraction
 * layer for accessing resources.
 * </p>
 * 
 * <p>
 * Some resources are protected and may only be accessed by
 * a valid session.
 * </p>
 * 
 * <p>
 * Any resource prefixed with <code>'.'</code> is considered
 * a hidden resource. The resources may not be accessed through
 * any methods in this class. To access these resources, use
 * normal File I/O.
 * </p>
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

    /**
     * The private directory.
     */
    public static final File PRIVATE_FILE = new File(PRIVATE_DIR).getAbsoluteFile();

    /**
     * The public directory.
     */
    public static final File PUBLIC_FILE = new File(PUBLIC_DIR).getAbsoluteFile();

    /**
     * Per-session directory.
     */
    public static final File SESSION_FILE = new File(SESSION_DIR).getAbsoluteFile();

    /**
     * Open an output stream to a private resource.
     * 
     * @param name The name of the resource.
     * @return The stream to the resource.
     * 
     * @throws IOException             If an I/O error occurs.
     * @throws IllegalAccessException  If the resource is not accessible. The
     *                                 existence of the resource is not checked.
     * @throws NoSuchResourceException If the resource does not exist.
     */
    public static InputStream readPrivate(String name)
            throws IOException, IllegalAccessException, NoSuchResourceException {
        String path = PRIVATE_DIR + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        if (f.getName().startsWith("."))
            throw new IllegalAccessException(path + " is a hidden resource");

        if (!f.exists())
            throw new NoSuchResourceException(path + " does not exist");

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
     * @throws IllegalAccessException If the resource is not accessible. The
     *                                existence of the resource is not checked.
     */
    public static OutputStream writePrivate(String name) throws IOException, IllegalAccessException {
        String path = PRIVATE_DIR + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        if (f.getName().startsWith("."))
            throw new IllegalAccessException(path + " is a hidden resource");

        // check if file is in private directory
        if (!FileUtils.isFileInDirectory(f, PRIVATE_FILE))
            throw new IllegalAccessException(path + " is not in the private directory");

        return new FileOutputStream(f);
    }

    /**
     * Open an input stream to a public resource.
     * 
     * @param name The name of the resource.
     * @return The stream to the resource.
     * 
     * @throws IllegalAccessException  If the resource is not accessible. The
     *                                 existence of the resource is not checked.
     * @throws IOException             If an I/O error occurs.
     * @throws NoSuchResourceException If the resource does not exist.
     */
    public static InputStream readPublic(String name)
            throws IOException, IllegalAccessException, NoSuchResourceException {
        String path = PUBLIC_DIR + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        if (f.getName().startsWith("."))
            throw new IllegalAccessException(path + " is a hidden resource");

        if (!f.exists())
            throw new NoSuchResourceException(path + " does not exist");

        // check if file is in public directory
        if (!FileUtils.isFileInDirectory(f, PUBLIC_FILE))
            throw new IllegalAccessException(path + " is not in the public directory");

        return new FileInputStream(f);
    }

    /**
     * Opens an input stream to a session resource.
     * 
     * @param name    The name of the resource.
     * @param session The session.
     * @return The stream to the resource.
     * 
     * @throws IOException             If an I/O error occurs.
     * @throws IllegalAccessException  If the resource is not accessible. The
     *                                 existence of the resource is not checked.
     * @throws NoSuchResourceException If the resource does not exist.
     */
    public static InputStream readSession(String name, Session session)
            throws IOException, IllegalAccessException, NoSuchResourceException {
        String sessionDirName = session.getJWT().getSubject();

        String path = SESSION_DIR + File.separatorChar + sessionDirName + File.separatorChar + name;
        File f = new File(path).getAbsoluteFile();

        if (f.getName().startsWith("."))
            throw new IllegalAccessException(path + " is a hidden resource");

        if (!f.exists())
            throw new NoSuchResourceException(path + " does not exist");

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
     * @param name    The name of the resource.
     * @param session The session.
     * @return The stream to the resource.
     * 
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException If the resource is not accessible. The
     *                                existence of the resource is not checked.
     */
    public static OutputStream writeSession(String name, Session session)
            throws IOException, IllegalAccessException {
        String sessionDirName = session.getJWT().getSubject();

        String path = SESSION_DIR + File.separatorChar + sessionDirName + File.separatorChar + name;

        File f = new File(path).getAbsoluteFile();

        if (f.getName().startsWith("."))
            throw new IllegalAccessException(path + " is a hidden resource");

        File sessionDir = new File(SESSION_DIR + File.separatorChar + sessionDirName);

        // check if file is in session directory for the session
        if (!FileUtils.isFileInDirectory(f, sessionDir))
            throw new IllegalAccessException(path + " is not in the session directory for the session");

        // create directories if it doesn't exist
        File parent = f.getParentFile();
        if (!parent.exists())
            parent.mkdirs();

        return new FileOutputStream(f);
    }

    // prevent instantiation
    private Resource() {
    }
}
