package com.smartnote.server;

public class ServerConfig {
    /**
     * The default port.
     */
    public static final int DEFAULT_PORT = 4567;

    private int port;
    private boolean usessl;

    public ServerConfig() {
        this.port = DEFAULT_PORT;
        this.usessl = false;
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
     * Sets the port.
     * 
     * @param port The port
     */
    public void setPort(int port) {
        this.port = port;
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
     * Sets whether or not SSL is used.
     * 
     * @param usessl Whether or not SSL is used
     */
    public void setUseSSL(boolean usessl) {
        this.usessl = usessl;
    }
}
