package com.smartnote.server.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * <p>
 * Represents an abstract resource. Resources can be retrieved
 * with the <code>ResourceSystem</code> class. The resource may
 * or may not exist.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.ResourceSystem
 */
public interface Resource {
    /**
     * Opens an input stream to the resource.
     * 
     * @return The input stream.
     * @throws SecurityException       When the current identity does not
     *                                 have read permission to the resource.
     * @throws NoSuchResourceException When the resource does not exist.
     * @throws IOException             If the stream could not be opened.
     */
    InputStream openInputStream() throws SecurityException, NoSuchResourceException, IOException;

    /**
     * Opens an output stream to the resource.
     * 
     * @return The output stream.
     * @throws SecurityException When the current identity does not
     *                           have write permission to the resource.
     * @throws IOException       If the stream could not be opened.
     */
    OutputStream openOutputStream() throws SecurityException, IOException;

    /**
     * Deletes the resource.
     * 
     * @throws SecurityException       When the current identity does not
     *                                 have delete permission to the resource.
     * @throws NoSuchResourceException When the resource does not exist.
     * @throws IOException             If the resource could not be deleted.
     */
    void delete() throws SecurityException, NoSuchResourceException, IOException;

    /**
     * Gets the size of the resource.
     * 
     * @return The size.
     * @throws SecurityException       When the current identity does not
     *                                 have read permission to the resource.
     * @throws NoSuchResourceException When the resource does not exist.
     * @throws IOException             If the size could not be retrieved.
     */
    long size() throws SecurityException, NoSuchResourceException, IOException;

    /**
     * Checks if the resource exists.
     * 
     * @return <code>true</code> if the resource exists, <code>false</code>
     *         otherwise.
     * @throws SecurityException When the current identity does not
     *                           have read permission to the resource.
     * @throws IOException       If the existence of the resource could not be
     *                           determined.
     */
    boolean exists() throws SecurityException, IOException;

    /**
     * Gets the name of the resource.
     * 
     * @return The name.
     */
    String getName();

    /**
     * Reads all bytes from the resource.
     * 
     * @return The bytes.
     * @throws SecurityException       When the current identity does not
     *                                 have read permission to the resource.
     * @throws NoSuchResourceException When the resource does not exist.
     * @throws IOException             If the bytes could not be read.
     */
    default byte[] readAllBytes() throws SecurityException, NoSuchResourceException, IOException {
        InputStream in = openInputStream();
        byte[] bytes = in.readAllBytes();
        in.close();
        return bytes;
    }

    /**
     * Reads all bytes from the resource and returns them as a string. This
     * call is equivalent to <code>new String(readAllBytes())</code>.
     * 
     * @return The string.
     * @throws SecurityException       When the current identity does not
     *                                 have read permission to the resource.
     * @throws NoSuchResourceException When the resource does not exist.
     * @throws IOException             If the bytes could not be read.
     */
    default String readToString() throws SecurityException, NoSuchResourceException, IOException {
        return new String(readAllBytes());
    }

    /**
     * Get the absolute path of the resource on the file system, if
     * it has one.
     * 
     * @return The path.
     * @throws SecurityException             When the current identity does not
     *                                       have read permission to the resource.
     * @throws UnsupportedOperationException If the resource does not have a path.
     */
    default Path getPath() throws SecurityException, UnsupportedOperationException {
        throw new UnsupportedOperationException("This resource does not have a path");
    }
}
