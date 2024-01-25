package com.smartnote.server.api.v1;

import java.io.File;

import com.smartnote.server.auth.Session;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Uploads a file to the server.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/upload")
public class Upload implements Route {
    /**
     * Directory within session directory to store uploaded files.
     */
    public static final String UPLOAD_DIR = "uploads/";

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // create session
        Session session = Session.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\": \"No session\"}";
        }

        // name of file to upload
        String filename = request.queryParams("name");
        if (filename == null) {
            response.status(400);
            return "{\"message\": \"Name was not specified\"}";
        }

        filename = filename.trim();
        filename = UPLOAD_DIR + filename;

        File file = session.getFile(filename);
        if (file == null) {
            response.status(400);
            return "{\"message\": \"Name is invalid\"}";
        }

        // write file to session directory
        try {
            session.writeSessionFile(filename, request.bodyAsBytes());
        } catch (IllegalAccessException e) {
            response.status(400);
            return "{\"message\": \"Name is invalid\"}";
        } catch (IllegalStateException e) {
            response.status(413);
            return "{\"message\": \"File is too large, quota is: " + Session.STORAGE_QUOTA + "\"}";
        }

        session.updateSession();
        session.writeToResponse(response);

        response.type("application/json");
        return "{\"message\": \"File was uploaded\"}";
    }
}
