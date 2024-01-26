package com.smartnote.server;

public class ResourceConfig {
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
}
