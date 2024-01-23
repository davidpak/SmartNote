package com.smartnote.server.api.v1;

import static spark.Spark.*;

import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

@ServerRoute(method = MethodType.POST, path = "/api/v1/generate")
public class Export implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // TODO: handle generate request
        halt(501);
        return null;
    }
}
