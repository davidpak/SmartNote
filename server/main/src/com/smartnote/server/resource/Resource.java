package com.smartnote.server.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.security.Permission;

import org.eclipse.jetty.util.IO;

/**
 * <p>Represents an abstract resource. Resources can be retrieved
 * with the <code>ResourceSystem</code> class.</p>.
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.ResourceSystem
 */
public interface Resource {
    /**
     * Opens an input stream to the resource.
     * 
     * @return The input stream.
     * @throws SecurityException When the current identity does not
     * have read permission to the resource.
     * @throws IOException If the stream could not be opened.
     */
    public InputStream openInputStream() throws SecurityException, IOException;

    /**
     * Opens an output stream to the resource.
     * 
     * @return The output stream.
     * @throws SecurityException When the current identity does not
     * have write permission to the resource.
     * @throws IOException If the stream could not be opened.
     */
    public OutputStream openOutputStream() throws SecurityException, IOException;

    /**
     * Deletes the resource.
     * 
     * @throws SecurityException When the current identity does not
     * have delete permission to the resource.
     * @throws IOException If the resource could not be deleted.
     */
    public void delete() throws SecurityException, IOException;

    /**
     * Find a sub-resource of this resource.
     * 
     * @param path The path to the resource.
     * @param permission The permission to use to find the resource.
     * @return The resource.
     * @throws AccessControlException When the given permission is not
     * sufficient to find the resource.
     * @throws NoSuchResourceException When the resource does not exist.
     * @throws IOException If the resource could not be found.
     */
    public Resource findResource(String path, Permission permission) throws AccessControlException, NoSuchResourceException, IOException;
}
