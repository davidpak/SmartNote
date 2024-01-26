package com.smartnote.server;

/**
 * Validates the state of an object.
 * 
 * @see com.smartnote.server.Config
 */
@FunctionalInterface
public interface Validator {
    void validate() throws IllegalStateException;    
}
