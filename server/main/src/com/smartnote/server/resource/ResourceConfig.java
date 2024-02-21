package com.smartnote.server.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.util.AbstractConfig;

/**
 * Stores configuration information for the resource system.
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Resource
 */
public class ResourceConfig extends AbstractConfig {
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

    /**
     * Default maximum upload size.
     */
    public static final long DEFAULT_MAX_UPLOAD_SIZE = 1024 * 1024 * 100; // 100 MiB

    /**
     * Default session quota.
     */
    public static final long DEFAULT_SESSION_QUOTA = 1024 * 1024 * 1024; // 1 GiB

    /**
     * Default upload directory.
     */
    public static final String DEFAULT_UPLOAD_DIR = "uploads";

    private String privateDir;
    private String publicDir;
    private String sessionDir;

    private long maxUploadSize;
    private long sessionQuota;

    private String uploadDir;

    /**
     * Creates a new ResourceConfig object with default values.
     */
    public ResourceConfig() {
        this.privateDir = DEFAULT_PRIVATE_DIR;
        this.publicDir = DEFAULT_PUBLIC_DIR;
        this.sessionDir = DEFAULT_SESSION_DIR;
        this.maxUploadSize = DEFAULT_MAX_UPLOAD_SIZE;
        this.sessionQuota = DEFAULT_SESSION_QUOTA;
        this.uploadDir = DEFAULT_UPLOAD_DIR;
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

    /**
     * Gets the maximum upload size.
     * 
     * @return The maximum upload size
     */
    public long getMaxUploadSize() {
        return maxUploadSize;
    }

    /**
     * Gets the session quota.
     * 
     * @return The session quota
     */
    public long getSessionQuota() {
        return sessionQuota;
    }

    /**
     * Gets the upload directory.
     * 
     * @return The upload directory
     */
    public String getUploadDir() {
        return uploadDir;
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler("privateDir", (p, a) -> {
            privateDir = p.next();
        }, "r");

        parser.addHandler("publicDir", (p, a) -> {
            publicDir = p.next();
        }, "u");

        parser.addHandler("sessionDir", (p, a) -> {
            sessionDir = p.next();
        }, "e");

        parser.addHandler("uploadDir", (p, a) -> {
            uploadDir = p.next();
        }, "p");
    }

    @Override
    public void validate() throws IllegalStateException {
        // No validation
        System.out.println("resource.privateDir=`" + privateDir + "`");
        System.out.println("resource.publicDir=`" + publicDir + "`");
        System.out.println("resource.sessionDir=`" + sessionDir + "`");
        System.out.println("resource.maxUploadSize=" + maxUploadSize);
        System.out.println("resource.sessionQuota=" + sessionQuota);
        System.out.println("resource.uploadDir=`" + uploadDir + "`");
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("privateDir", privateDir);
        json.addProperty("publicDir", publicDir);
        json.addProperty("sessionDir", sessionDir);
        json.addProperty("maxUploadSize", maxUploadSize);
        json.addProperty("sessionQuota", sessionQuota);
        json.addProperty("uploadDir", uploadDir);
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        JsonElement elem;

        elem = json.get("privateDir");
        if (elem != null && elem.isJsonPrimitive())
            privateDir = elem.getAsString();

        elem = json.get("publicDir");
        if (elem != null && elem.isJsonPrimitive())
            publicDir = elem.getAsString();

        elem = json.get("sessionDir");
        if (elem != null && elem.isJsonPrimitive())
            sessionDir = elem.getAsString();

        elem = json.get("maxUploadSize");
        if (elem != null && elem.isJsonPrimitive())
            maxUploadSize = elem.getAsLong();

        elem = json.get("sessionQuota");
        if (elem != null && elem.isJsonPrimitive())
            sessionQuota = elem.getAsLong();

        elem = json.get("uploadDir");
        if (elem != null && elem.isJsonPrimitive())
            uploadDir = elem.getAsString();
    }
}
