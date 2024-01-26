package com.smartnote.server;

import com.smartnote.server.cli.CommandLineHandler;
import com.smartnote.server.cli.CommandLineParser;

/**
 * <p>Stores configuration information for the server.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Server
 */
public class Config implements CommandLineHandler, Validator {

    private ServerConfig serverConfig;
    private ResourceConfig resourceConfig;

    /**
     * Creates a new Config object with default values.
     */
    public Config() {
        this.serverConfig = new ServerConfig();
        this.resourceConfig = new ResourceConfig();
    }

    /**
     * Gets the server configuration.
     * 
     * @return The server configuration
     */
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    /**
     * Gets the resource configuration.
     * 
     * @return The resource configuration
     */
    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    @Override
    public void validate() throws IllegalStateException {
        serverConfig.validate();
        resourceConfig.validate();
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler(serverConfig);
        parser.addHandler(resourceConfig);
    }
}
