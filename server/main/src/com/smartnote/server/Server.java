package com.smartnote.server;

import static spark.Spark.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnote.server.api.v1.Export;
import com.smartnote.server.api.v1.Fetch;
import com.smartnote.server.api.v1.Generate;
import com.smartnote.server.api.v1.Login;
import com.smartnote.server.api.v1.Remove;
import com.smartnote.server.api.v1.RescInfo;
import com.smartnote.server.api.v1.Upload;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.cli.ExitEarlyEarlyException;
import com.smartnote.server.cli.NoSuchSwitchException;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.CryptoUtils;
import com.smartnote.server.util.ServerRoute;

import spark.Route;

/**
 * <p>
 * The server. Handles initialization and shutdown.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 * @see com.smartnote.server.Resource
 * @see com.smartnote.server.util.ServerRoute
 */
public class Server {

    /**
     * The version.
     */
    public static final String VERSION = "1.0.0";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    /**
     * The server.
     */
    private static final Server SERVER = new Server();

    /**
     * Gets the instance of the server.
     * 
     * @return The server.
     */
    public static Server getServer() {
        return SERVER;
    }

    private Config config; // the server config
    private ResourceSystem resourceSystem; // the resource system
    private SessionManager sessionManager; // the session manager

    public static void main(String[] args) {
        try {
            SERVER.init(args);
        } catch (ExitEarlyEarlyException e) {
            System.exit(e.getCode());
        }
    }

    // Only allow one instance
    private Server() {
    }

    /**
     * Get the server config.
     * 
     * @return The config.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Gets the resource system.
     * 
     * @return The resource system.
     */
    public ResourceSystem getResourceSystem() {
        return resourceSystem;
    }

    /**
     * Gets the session manager.
     * 
     * @return The session manager.
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Initializes the server.
     * 
     * @param args The command line arguments.
     */
    public void init(String[] args) {
        loadConfig(args);
        initCrypto();
        initResourceSystem();
        initSessionManager();
        initNetworking();
    }

    // Loads the config file and parses the command line
    private void loadConfig(String[] args) {
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
            throw new ExitEarlyEarlyException(1);
        } catch (ExitEarlyEarlyException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            printHelp();
            throw new ExitEarlyEarlyException(1);
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
    }

    // Initializes cryptographic utilities
    private void initCrypto() {
        // initialize the crypto utils
        try {resourceSystem = new ResourceSystem(config.getResourceConfig());
            CryptoUtils.init(null);
        } catch (Exception e) {
            LOG.error("Failed to initialize CryptoUtils");
            e.printStackTrace();
        }
    }

    // Initializes the resource system
    private void initResourceSystem() {
        resourceSystem = new ResourceSystem(config.getResourceConfig());
    }

    // Initializes the session manager
    private void initSessionManager() {
        // initialize the session manager
        sessionManager = new SessionManager();
        sessionManager.forceGc();
    }

    // Initializes the networking stuff (Spark)
    private void initNetworking() {
        ServerConfig serverConfig = config.getServerConfig();

        // handle exceptions
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("Internal server error");
        });

        port(serverConfig.getPort());

        after((req, res) -> {
            // CORS
            res.header("Access-Control-Allow-Origin", serverConfig.getOrigin());
            res.header("Access-Control-Allow-Methods", "GET, POST");
            res.header("Access-Control-Allow-Credentials", "true");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.header("Access-Control-Expose-Headers", "Content-Type");
        });

        // Add RPC routes
        addRoute(Export.class);
        addRoute(Fetch.class);
        addRoute(Generate.class);
        addRoute(Upload.class);
        addRoute(Login.class);
        addRoute(Upload.class);
        addRoute(Remove.class);
        addRoute(RescInfo.class);
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
                options(path, (req, res) -> {
                    res.status(200);
                    res.header("Allow", "GET");
                    res.type("application/json");
                    return "{\"message\":\"OK\"}";
                });
                break;
            case POST:
                post(path, r);
                options(path, (req, res) -> {
                    res.status(200);
                    res.header("Allow", "POST");
                    res.type("application/json");
                    return "{\"message\":\"OK\"}";
                });
                break;
            case DELETE:
                delete(path, r);
                options(path, (req, res) -> {
                    res.status(200);
                    res.header("Allow", "DELETE");
                    res.type("application/json");
                    return "{\"message\":\"OK\"}";
                });
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
        System.out.printf("  -v, --version            Print the version\n");
        System.out.printf("  -p, --port <port>        Specify the port to listen on\n");
        System.out.printf("  -s, --ssl                Enable SSL\n");
        System.out.printf("  -i, --insecure           Disable SSL (default)\n");
        System.out.printf("  -c, --cert <file>        Specify the certificate file\n");
        System.out.printf("  -r, --private-dir <dir>  Specify the private directory\n");
        System.out.printf("  -u, --public-dir <dir>   Specify the public directory\n");
        System.out.printf("  -e, --session-dir <dir>  Specify the per-session directory\n");
        System.out.printf("  -p, --upload-dir <dir>   Specify the upload directory\n");
    }

    /**
     * Prints the version.
     */
    private static void printVersion() {
        System.out.printf("SmartNote Server v%s\n", VERSION);

        try {
            Class<Server> clazz = Server.class;
            String className = clazz.getSimpleName() + ".class";
            String classPath = clazz.getResource(className).toString();
            if (!classPath.startsWith("jar")) {
                System.out.printf("Built from source\n");
            } else {
                String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                "/META-INF/MANIFEST.MF";
                Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                Attributes attr = manifest.getMainAttributes();
                String buildTime = attr.getValue("Build-Time");
                System.out.printf("Build time: %s\n", buildTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
