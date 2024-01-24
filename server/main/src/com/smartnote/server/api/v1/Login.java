package com.smartnote.server.api.v1;

import static spark.Spark.halt;

import com.smartnote.server.auth.Session;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Login RPC. Creates a new session.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/login")
public class Login implements Route {
    
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // no authorization header allowed
        if (request.queryParams("Authorization") != null) {
            halt(400);
            return null;
        }

        // create session
        Session session = Session.createSession();
    
        session.writeToResponse(response);
        response.type("text/plain");
        return "Success";
    }
}
