package com.smartnote.server.cli;

/**
 * <p>Used to add multiple handlers to a <code>CommandLineParser</code>.</p>
 * 
 * @author Ethan Vrhel
 * @see CommandLineParser
 */
@FunctionalInterface
public interface CommandLineHandler {
    /**
     * Adds handlers to the parser.
     * 
     * @param parser The parser.
     */
    void addHandlers(CommandLineParser parser);
}
