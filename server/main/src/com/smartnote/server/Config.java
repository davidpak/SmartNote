package com.smartnote.server;

/**
 * The server configuration.
 * 
 * @author Ethan Vrhel
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
                    System.err.println("Missing port number");
                    printHelp();
                    return 1;
                }
            } else if (a.equals("-s") || a.equals("--ssl")) {
                usessl = true;
            } else if (a.equals("-i") || a.equals("--insecure")) {
                usessl = false;
            } else {
                System.err.println("Unknown option: " + a);
                printHelp();
                return 1;
            }
        }

        return -1;
    }

    public int getPort() {
        return port;
    }

    public boolean useSSL() {
        return usessl;
    }
}
