package com.smartnote.server.api.v1;

import static spark.Spark.*;

import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

@ServerRoute(path = "/api/v1/remove", method = MethodType.POST)
public class Remove implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        halt(501);
        return null;
    }
    
}
