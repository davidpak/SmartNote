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
     * The default certificate file.
     */
    public static final String DEFAULT_CERT_FILE = "cert.pem";

    private int port;
    private boolean usessl;
    private String certFile;

    /**
     * Creates a new ServerConfig object with default values.
     */
    public ServerConfig() {
        this.port = DEFAULT_PORT;
        this.usessl = false;
        this.certFile = DEFAULT_CERT_FILE;
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
     * Gets whether or not SSL is used.
     * 
     * @return Whether or not SSL is used
     */
    public boolean getUseSSL() {
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

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler("port", (p, a) -> {
            port = p.nextInt();
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
    }

    @Override
    public void validate() throws IllegalStateException {
        if (port < 0 || port > 65535)
            throw new IllegalStateException("Port must be between 0 and 65535");

        if (usessl)
            throw new IllegalStateException("SSL is not yet supported");

        if (usessl && !new File(certFile).exists())
            throw new IllegalStateException("Certificate file does not exist");
    }

    @Override
    public JsonObject writeJSON(JsonObject object) {
        object.addProperty("port", port);
        object.addProperty("ssl", usessl);
        object.addProperty("cert", certFile);
        return object;
    }

    @Override
    public void loadJSON(JsonObject json) {
        JsonElement elem;

        elem = json.get("port");
        if (elem != null && elem.isJsonPrimitive())
            port = elem.getAsInt();

        elem = json.get("ssl");
        if (elem != null && elem.isJsonPrimitive())
            usessl = elem.getAsBoolean();

        elem = json.get("cert");
        if (elem != null && elem.isJsonPrimitive())
            certFile = elem.getAsString();
    }
}
