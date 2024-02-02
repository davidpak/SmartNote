package com.smartnote.server.resource;

import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Path;

/**
 * Thrown when a resource does not exist.
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Resource
 */
public class NoSuchResourceException extends FileNotFoundException {
    private static final long serialVersionUID = 1L;

    public NoSuchResourceException() {
        super();
    }

    public NoSuchResourceException(String message) {
        super(message);
    }
    
    public NoSuchResourceException(URI uri) {
        super(uri.toString());
    }

    public NoSuchResourceException(Path path) {
        super(path.toString());
    }
}
