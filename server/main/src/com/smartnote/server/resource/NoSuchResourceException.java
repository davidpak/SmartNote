package com.smartnote.server.resource;

import java.io.IOException;
import java.net.URI;

/**
 * Thrown when a resource does not exist.
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Resource
 */
public class NoSuchResourceException extends IOException {
    private static final long serialVersionUID = 1L;

    public NoSuchResourceException() {
        super();
    }

    public NoSuchResourceException(String message) {
        super(message);
    }

    public NoSuchResourceException(Throwable cause) {
        super(cause);
    }

    public NoSuchResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NoSuchResourceException(URI uri) {
        super(uri.toString());
    }
}