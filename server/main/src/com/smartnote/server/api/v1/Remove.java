package com.smartnote.server.api.v1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;

import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MIME;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>
 * Delete a file from the server.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.Resource
 */
@ServerRoute(path = "/api/v1/remove", method = MethodType.DELETE)
public class Remove implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type(MIME.JSON);

        ResourceSystem system = Server.getServer().getResourceSystem();
        SessionManager sessionManager = Server.getServer().getSessionManager();

        Session session = sessionManager.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\": \"No session\"}";
        }

        String filename = request.queryParams("name");
        if (filename == null) {
            response.status(400);
            return "{\"message\": \"Name was not specified\"}";
        }

        // find resource
        Resource resource;
        try {
            resource = system.findResource(filename, session.getPermission());
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\": \"Access denied\"}";
        } catch (InvalidPathException e) {
            response.status(400);
            return "{\"message\": \"Invalid path\"}";
        } catch (NoSuchResourceException e) {
            response.status(404);
            return "{\"message\": \"Resource not found\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\": \"Could open resource\"}";
        }

        // delete resource
        try {
            resource.delete();
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\": \"Access denied\"}";
        } catch (FileNotFoundException e) {
            response.status(404);
            return "{\"message\": \"Resource not found\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\": \"Could not delete resource\"}";
        }

        session.updateSession(sessionManager);
        session.writeToResponse(response);

        return "{\"message\":\"File deleted\"}";
    }

}
