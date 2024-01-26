package com.smartnote.server;

import static spark.Spark.*;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnote.server.api.v1.Export;
import com.smartnote.server.api.v1.Fetch;
import com.smartnote.server.api.v1.Generate;
import com.smartnote.server.api.v1.Login;
import com.smartnote.server.api.v1.Upload;
import com.smartnote.server.auth.Session;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.cli.ExitEarlyEarlyException;
import com.smartnote.server.cli.NoSuchSwitchException;
import com.smartnote.server.util.CryptoUtils;
import com.smartnote.server.util.FileUtils;
import com.smartnote.server.util.ServerRoute;

import spark.Route;

/**
 * <p>The server. Handles initialization and shutdown.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 * @see com.smartnote.server.Resource
 * @see com.smartnote.server.util.ServerRoute
 */
public class Server {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    /**
     * The server.
     */
    public static final Server SERVER = new Server();
    
    /**
     * The version.
     */
    public static final String VERSION = "1.0.0";

    private Config config; // the server config
  
    public static void main(String[] args) {
        SERVER.init(args);
    }

    // Only allow one instance
    private Server() {}

    /**
     * Get the server config.
     * 
     * @return The config.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Initializes the server.
     * 
     * @param args The command line arguments.
     */
    public int init(String[] args) {
        boolean configLoaded = false;

        // load the config file
        try {
            config = Config.loadConfig();
            configLoaded = true;
        } catch (Exception e) {
            config = new Config();
            configLoaded = false;
        }

        // set up the command line parser

        CommandLineParser parser = new CommandLineParser(args);
        
        parser.addHandler("help", (p, s) -> {
            printHelp();
            throw new ExitEarlyEarlyException(0);
        });

        parser.addHandler("version", (p, s) -> {
            printVersion();
            throw new ExitEarlyEarlyException(0);
        });

        parser.addHandler(config);

        // parse the command line
        try {
            parser.parse();
        } catch (NoSuchSwitchException e) {
            System.err.println("Unknown switch: " + e.getMessage());
            return 1;
        } catch (ExitEarlyEarlyException e) {
            return e.getCode();
        } catch (Exception e) {
            e.printStackTrace();
            printHelp();
            return 1;
        }

        if (configLoaded)
            LOG.info("Loaded config file: " + Config.CONFIG_FILE);
        else {
            LOG.error("Failed to load " + Config.CONFIG_FILE + ", using default config");
            try {
                Config.writeConfig(config);
            } catch (Exception e) {
                LOG.error("Failed to write default config");
                e.printStackTrace();
            }
        }

        // validate the config
        config.validate();

        // initialize the crypto utils
        try {
            CryptoUtils.init(null);
        } catch (Exception e) {
            LOG.error("Failed to initialize CryptoUtils");
            e.printStackTrace();
            return 1;
        }

        // remove old sessions
        Session.forceGc();

        // handle exceptions
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("Internal server error");
        });

        port(config.getServerConfig().getPort());

        // Add RPC routes
        addRoute(Export.class);
        addRoute(Fetch.class);
        addRoute(Generate.class);
        addRoute(Upload.class);
        addRoute(Login.class);
        addRoute(Upload.class);

        return 0;
    }

    /**
     * Adds a route to the server.
     * 
     * @param routeClass The class to add. Must be annotated with ServerRoute.
     */
    private void addRoute(Class<? extends Route> routeClass) {
        ServerRoute route = routeClass.getAnnotation(ServerRoute.class);
        if (route == null)
            throw new RuntimeException("Class " + routeClass.getName() + " is not annotated with ServerRoute");

        String path = route.path();
        if (path == null)
            throw new RuntimeException("Class " + routeClass.getName() + " has a null path");

        // try to create an instance of the route
        Route r;
        try {
            Constructor<? extends Route> c = routeClass.getConstructor();
            r = c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add route", e);
        }

        // register the route with Spark
        switch (route.method()) {
            case GET:
                get(path, r);
                break;
            case POST:
                post(path, r);
                break;
            default:
                throw new IllegalArgumentException("Unknown method type: " + route.method());
        }

        LOG.info("Added route: " + route.method() + " " + path);
    }

    /**
     * Prints the help message.
     */
    private static void printHelp() {
        System.out.printf("Usage: java -jar server.jar [options]\n");
        System.out.printf("Options:\n");
        System.out.printf("  -h, --help               Print this help message\n");
        System.out.printf("  -p, --port <port>        Specify the port to listen on\n");
        System.out.printf("  -s, --ssl                Enable SSL\n");
        System.out.printf("  -i, --insecure           Disable SSL (default)\n");
        System.out.printf("  -c, --cert <file>        Specify the certificate file\n");
        System.out.printf("  -r, --private-dir <dir>  Specify the private directory\n");
        System.out.printf("  -u, --public-dir <dir>   Specify the public directory\n");
        System.out.printf("  -e, --session-dir <dir>  Specify the per-session directory\n");
    }

    private static void printVersion() {
        System.out.printf("SmartNote Server v%s\n", VERSION);
    }
}
