package com.smartnote.server.resource;

import com.smartnote.server.cli.CommandLineHandler;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.util.Validator;

/**
 * Stores configuration information for the resource system.
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Resource
 * @see com.smartnote.server.cli.CommandLineParser
 */
public class ResourceConfig implements CommandLineHandler, Validator {
    /**
     * Default private directory.
     */
    public static final String DEFAULT_PRIVATE_DIR = "private";

    /**
     * Default public directory.
     */
    public static final String DEFAULT_PUBLIC_DIR = "public";

    /**
     * Default per-session directory.
     */
    public static final String DEFAULT_SESSION_DIR = "sessions";

    private String privateDir;
    private String publicDir;
    private String sessionDir;
    
    /**
     * Creates a new ResourceConfig object with default values.
     */
    public ResourceConfig() {
        this.privateDir = DEFAULT_PRIVATE_DIR;
        this.publicDir = DEFAULT_PUBLIC_DIR;
        this.sessionDir = DEFAULT_SESSION_DIR;
    }

    /**
     * Gets the private directory.
     * 
     * @return The private directory
     */
    public String getPrivateDir() {
        return privateDir;
    }

    /**
     * Gets the public directory.
     * 
     * @return The public directory
     */
    public String getPublicDir() {
        return publicDir;
    }

    /**
     * Gets the per-session directory.
     * 
     * @return The per-session directory
     */
    public String getSessionDir() {
        return sessionDir;
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler("private-dir", (p, a) -> {
            privateDir = p.next();
        }, "r");

        parser.addHandler("public-dir", (p, a) -> {
            publicDir = p.next();
        }, "u");

        parser.addHandler("session-dir", (p, a) -> {
            sessionDir = p.next();
        }, "e");
    }

    @Override
    public void validate() throws IllegalStateException {
        // No validation
    }
}
