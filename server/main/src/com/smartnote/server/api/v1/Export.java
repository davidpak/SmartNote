package com.smartnote.server.api.v1;

import static spark.Spark.*;

import com.smartnote.server.Server;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Exports generated summaries to files or remote locations.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/generate")
public class Export implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        
        String body = request.body();
        if (body == null) {
            response.status(400);
            return "{\"message\":\"Missing export options\"}";
        }


        return "{\"message\":\"Exported successfully\"}";
    }
}
