package com.smartnote.server;

import static spark.Spark.*;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnote.server.auth.Session;
import com.smartnote.server.rpc.Upload;
import com.smartnote.server.util.ServerRoute;

import spark.Route;

/**
 * The server.
 * 
 * @author Ethan Vrhel
 */
public class Server {

    /**
     * The logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger("SmartNote Server");

    /**
     * The server.
     */
    private static Server SERVER;

    private Config config;

    public static void main(String[] args) {
        SERVER = new Server();
        SERVER.init(args);
    }

    // don't allow instantiation
    private Server() {
        this.config = new Config();
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
     * Initializes the server.
     * 
     * @param args The command line arguments.
     */
    public int init(String[] args) {
        int rc = config.parseCommandLine(args);
        if (rc >= 0)
            return rc;

        validate();

        Session.init();

        // handle exceptions
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("Internal server error");
        });

        port(config.getPort());

        addRoute(Upload.class);

        return 0;
    }

    /**
     * Validates the server configuration. Run this before starting
     * the server.
     */
    private void validate() {
        if (config.useSSL()) {
            System.out.printf("SSL is currently unsupported\n");
            System.exit(1);
        }
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
}
