package com.smartnote.server.api.v1;

import static spark.Spark.*;

import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Generates summaries from uploaded files.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/generate")
public class Generate implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // TODO: handle generate request
        halt(501);
        return null;
    }
}
