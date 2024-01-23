package com.smartnote.server.rpc;

import static spark.Spark.*;

import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles uploading of files to the server.
 * 
 * @author Ethan Vrhel
 */
@ServerRoute(method = MethodType.POST, path = "/upload")
public class Upload implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // TODO: handle file upload
        halt(501);
        return null;
    }
}
