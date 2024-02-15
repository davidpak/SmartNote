package com.smartnote.server.api.v1;

import static spark.Spark.*;

import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Fetches resources on server.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.GET, path = "/api/v1/fetch")
public class Fetch implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        halt(501);
        return null;
    }

}
