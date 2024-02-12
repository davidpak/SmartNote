package com.smartnote.server.api.v1;

import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>
 * Login RPC. Creates a new session.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/login")
public class Login implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type("application/json");

        SessionManager sessionManager = Server.getServer().getSessionManager();
        Session session = sessionManager.getSession(request);
        if (session != null) {
            session.store();
            session.updateSession(sessionManager);
            session.writeToResponse(response);
            return "{\"message\": \"Session renewed\"}";
        }

        // create session
        session = Server.getServer().getSessionManager().createSession();

        session.writeToResponse(response);
        return "{\"message\": \"Session created\"}";
    }
}
