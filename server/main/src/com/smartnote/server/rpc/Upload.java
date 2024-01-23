package com.smartnote.server.rpc;

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

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // create session
        Session session = Session.getSession(request);
        if (session == null)
            session = Session.createSession();

        // name of file to upload
        String filename = request.queryParams("filename");
        if (filename == null) {
            response.status(400);
            return "{\"message\": \"filename not specified\"}";
        }

        // write file to session directory
        try {
            session.writeSessionFile(filename, request.bodyAsBytes());
        } catch (IllegalAccessException e) {
            response.status(400);
            return "{\"message\": \"invalid filename\"}";
        }

        session.updateSession();
        session.writeToResponse(response);

        response.type("application/json");
        return "{\"message\": \"success\"}";
    }
}
