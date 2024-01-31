package com.smartnote.server.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * <p>Wraps a resource and makes it read-only.</p>
 * 
 * <p>For example, if a resource is a file, then the file cannot be written to
 * or deleted.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.Resource
 */
public class ReadOnlyResource implements Resource {   
    private Resource resource;

    /**
     * Creates a new read-only resource.
     * 
     * @param resource The resource to wrap.
     */
    public ReadOnlyResource(Resource resource) {
        this.resource = Objects.requireNonNull(resource, "resource must not be null");
    }

    @Override
    public InputStream openInputStream() throws SecurityException, IOException {
        return resource.openInputStream();
    }

    @Override
    public OutputStream openOutputStream() throws SecurityException, IOException {
        throw new SecurityException("No write permission");
    }

    @Override
    public void delete() throws SecurityException, IOException {
        throw new SecurityException("No delete permission");
    }

    @Override
    public long size() throws SecurityException, IOException {
        return resource.size();
    }
}
