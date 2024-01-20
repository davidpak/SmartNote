package com.smartnote.server;

public class Server {
    public static void main(String[] args) {
        processCommandLine(args);
    }

    private static void processCommandLine(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-h") || a.equals("--help")) {
                printHelp();
                System.exit(0);
            } else {
                System.err.println("Unknown option: " + a);
                printHelp();
                System.exit(1);
            }
        }
    }

    private static void printHelp() {
        System.out.printf("Usage: java -jar server.jar [options]\n");
        System.out.printf("Options:\n");
        System.out.printf("  -h, --help           Print this help message\n");
    }
}
