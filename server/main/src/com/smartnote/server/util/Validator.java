package com.smartnote.server.util;

/**
 * Validates the state of an object.
 * 
 * @see com.smartnote.server.Config
 */
@FunctionalInterface
public interface Validator {

    /**
     * Validates the state of the object. Does nothing if the state is valid.
     * 
     * @throws IllegalStateException If the state is invalid.
     */
    void validate() throws IllegalStateException;    
}
