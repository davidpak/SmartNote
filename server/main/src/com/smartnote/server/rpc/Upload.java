package com.smartnote.server.rpc;

import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnote.server.auth.Session;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles uploading of files to the server.
 * 
 * @author Ethan Vrhel
 */
@ServerRoute(method = MethodType.POST, path = "/upload")
public class Upload implements Route {
    private static final Logger LOG = LoggerFactory.getLogger(Upload.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // TODO: handle file upload

        // create session
        Session session = Session.getSession(request);
        if (session == null)
            session = Session.createSession();
        Session.storeSession(session, response);

        halt(501);
        return null;
    }
}
