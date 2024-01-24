package com.smartnote.server;

import java.security.Provider;
import java.security.Security;

/**
 * <p>Stores configuration information for the server.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Server
 */
public class Config {
    /**
     * The default port.
     */
    public static final int DEFAULT_PORT = 4567;

    /**
     * Prints the help message.
     */
    private static void printHelp() {
        System.out.printf("Usage: java -jar server.jar [options]\n");
        System.out.printf("Options:\n");
        System.out.printf("  -h, --help           Print this help message\n");
        System.out.printf("  -p, --port <port>    Specify the port to listen on\n");
        System.out.printf("  -s, --ssl            Enable SSL\n");
        System.out.printf("  -i, --insecure       Disable SSL (default)\n");
        System.out.printf("  -g, --algorithms     Print the supported algorithms\n");
    }

    /**
     * Print supported cryptographic algorithms.
     */
    private static void printAlgorithms() {
        // this was used earler, but probably can be removed
        System.out.printf("Supported algorithms:\n");

        Provider[] providers = Security.getProviders();
        for (Provider p : providers) {
            System.out.printf("Provider: %s\n", p.getName());
            for (Provider.Service s : p.getServices())
                System.out.printf("  Algorithm: %s\n", s.getAlgorithm());
        }
    }

    private int port;
    private boolean usessl;

    /**
     * Creates a new Config object with default values.
     */
    public Config() {
        this.port = DEFAULT_PORT;
        this.usessl = false;
    }

    /**
     * Parse command line options into a Config object.
     * 
     * @param args The command line arguments.
     * @return The exit code. If less than 0, the program should
     *         continue.
     */
    public int parseCommandLine(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-h") || a.equals("--help")) {
                printHelp();
                return 0;
            } else if (a.equals("-p") || a.equals("--port")) {
                if (i + 1 < args.length) {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                } else {
                    System.err.printf("Missing port number\n");
                    printHelp();
                    return 1;
                }
            } else if (a.equals("-s") || a.equals("--ssl")) {
                usessl = true;
            } else if (a.equals("-i") || a.equals("--insecure")) {
                usessl = false;
            } else if (a.equals("-g") || a.equals("--algorithms")) {
                printAlgorithms();
                return 0;
            } else {
                System.err.println("Unknown option: " + a);
                printHelp();
                return 1;
            }
        }

        return -1;
    }

    /**
     * Gets the port.
     * 
     * @return The port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets whether or not to use SSL.
     * 
     * @return Whether or not to use SSL.
     */
    public boolean useSSL() {
        return usessl;
    }
}
