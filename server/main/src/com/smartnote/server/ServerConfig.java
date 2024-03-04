package com.smartnote.server;

import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.util.AbstractConfig;

/**
 * <p>
 * Stores configuration information for the server.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Server
 * @see com.smartnote.server.cli.CommandLineParser
 */
public class ServerConfig extends AbstractConfig {
    /**
     * The default port.
     */
    public static final int DEFAULT_PORT = 4567;

    /**
     * The default host.
     */
    public static final String DEFAULT_HOST = "localhost";

    /**
     * The default certificate file.
     */
    public static final String DEFAULT_CERT_FILE = "cert.pem";

    /**
     * The default origin for CORS.
     */
    public static final String DEFAULT_ORIGIN = "http://localhost:8080";

    private int port;
    private String host;
    private boolean usessl;
    private String certFile;
    private String origin;
    private boolean debug;

    /**
     * Creates a new ServerConfig object with default values.
     */
    public ServerConfig() {
        this.port = DEFAULT_PORT;
        this.host = DEFAULT_HOST;
        this.usessl = false;
        this.certFile = DEFAULT_CERT_FILE;
        this.origin = null;
        this.debug = false;
    }

    /**
     * Gets the port.
     * 
     * @return The port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the host.
     * 
     * @return The host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets whether or not SSL is used.
     * 
     * @return Whether or not SSL is used
     */
    public boolean useSSL() {
        return usessl;
    }

    /**
     * Gets the certificate file.
     * 
     * @return The certificate file
     */
    public String getCertFile() {
        return certFile;
    }

    /**
     * Gets the origin for CORS.
     * 
     * @return The origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Returns whether or not debug mode is enabled.
     * 
     * @return Whether or not debug mode is enabled
     */
    public boolean getDebug() {
        return debug;
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler("port", (p, a) -> {
            port = p.nextInt();
        });

        parser.addHandler("host", (p, a) -> {
            host = p.next();
        });

        parser.addHandler("ssl", (p, a) -> {
            usessl = true;
        });

        parser.addHandler("insecure", (p, a) -> {
            usessl = false;
        });

        parser.addHandler("cert", (p, a) -> {
            certFile = p.next();
        });

        parser.addHandler("origin", (p, a) -> {
            origin = p.next();
        });
    }

    @Override
    public void validate() throws IllegalStateException {
        if (port < 0 || port > 65535)
            throw new IllegalStateException("server.port must be between 0 and 65535");
        System.out.println("server.port=`" + port + "`");

        if (usessl)
            throw new IllegalStateException("SSL is not yet supported, but is enabled through server.ssl");
        System.out.println("server.usessl=" + usessl);

        if (usessl && !new File(certFile).exists())
            throw new IllegalStateException("server.certFile does not exist");
        System.out.println("server.certFile=`" + certFile + "`");

        if (origin == null)
            throw new IllegalStateException("Origin must be set through server.origin");
        System.out.println("server.origin=`" + origin + "`");

        System.out.println("server.debug=" + debug);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("port", port);
        json.addProperty("host", host);
        json.addProperty("ssl", usessl);
        json.addProperty("cert", certFile);
        json.addProperty("origin", origin);
        json.addProperty("debug", debug);
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        JsonElement elem;

        elem = json.get("port");
        if (elem != null && elem.isJsonPrimitive())
            port = elem.getAsInt();

        elem = json.get("host");
        if (elem != null && elem.isJsonPrimitive())
            host = elem.getAsString();

        elem = json.get("ssl");
        if (elem != null && elem.isJsonPrimitive())
            usessl = elem.getAsBoolean();

        elem = json.get("cert");
        if (elem != null && elem.isJsonPrimitive())
            certFile = elem.getAsString();

        elem = json.get("origin");
        if (elem != null && elem.isJsonPrimitive())
            origin = elem.getAsString();

        elem = json.get("debug");
        if (elem != null && elem.isJsonPrimitive())
            debug = elem.getAsBoolean();
    }
}
