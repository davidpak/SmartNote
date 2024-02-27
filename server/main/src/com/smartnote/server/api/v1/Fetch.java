package com.smartnote.server.api.v1;

import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.http.protocol.RequestDate;
import org.apache.tika.Tika;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.ServerConfig;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;


/**
 * <p>Fetches resources on server.</p>
 * 
 * @author Ethan Vrhel
 * @author Jaden Summerville
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.GET, path = "/api/v1/fetch")
public class Fetch implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Get session
        SessionManager sessionManager = Server.getServer().getSessionManager();
        Session session = sessionManager.getSession(request);

        // Get name
        String name = request.queryParams("name");
        if (name == null) {
            response.status(400);
            return "{\"message\": \"Name was not specified\"}";
        }
        name = name.trim();

        byte[] body;
        try{
            // get file
            Resource resource = Server.getServer().getResourceSystem().findResource(name, session.getPermission());
            //load file
            InputStream inputStream = resource.openInputStream();
            //add info to body
            body = inputStream.readAllBytes();
            //send
        } catch(SecurityException e) {
            response.status(403);
            return "{\"message\":\"Access denied\"}";
        } catch (FileNotFoundException e) {
            // File not found
            response.status(404);
            return "{\"message\":\"File not found\"}";
        }

        response.header("Content-Type", new Tika().detect(body));
        response.status(200); // OK

        return body;
    }
}
