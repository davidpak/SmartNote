package com.smartnote.server.api.v1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
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
        SessionManager sessionManager = Server.getServer().getSessionManager();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();

        long startTime = System.currentTimeMillis();

        Session session = sessionManager.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\":\"No session\"}";
        }

        Permission permission = session.getPermission();

        Gson gson = new Gson();
        JsonObject generateJson = gson.fromJson(request.body(), JsonObject.class);

        JsonObject generalOptions = getObjectOrNull(generateJson, "general");
        if (generalOptions == null) {
            response.status(400);
            return "{\"message\":\"Missing field general\"}";
        }

        JsonArray files = getArrayOrNull(generalOptions, "files");
        if (files == null) {
            response.status(400);
            return "{\"message\":\"Missing field general.files\"}";
        }

        JsonObject llmOptions = getObjectOrNull(generateJson, "llm");
        if (llmOptions == null) {
            response.status(400);
            return "{\"message\":\"Missing field llm\"}";
        }

        double verbosity = getNumberOrDefault(llmOptions, "verbosity", 0.5);

        // TODO: replace with actual implementation
        final String DEBUG_RESOURCE = "public:output.md";

        Resource resource;
        
        try {
            resource = resourceSystem.findResource(DEBUG_RESOURCE, permission);
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\":\"Permission denied\"}";
        } catch (InvalidPathException e) {
            response.status(400);
            return "{\"message\":\"Invalid path\"}";
        } catch (NoSuchResourceException e) {
            response.status(404);
            return "{\"message\":\"Resource not found\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\":\"Internal server error\"}";
        }

        ParsedMarkdown md;
        InputStream in = null;
        try {
            in = resource.openInputStream();
            md = ParsedMarkdown.parse(new String(in.readAllBytes()));
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\":\"Permission denied\"}";
        } catch (NoSuchResourceException e) {
            response.status(404);
            return "{\"message\":\"Resource not found\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\":\"Internal server error\"}";
        } catch (IllegalArgumentException e) {
            response.status(500);
            return "{\"message\":\"Generated content is invalid\"}";
        } finally {
            if (in != null)
                in.close();
        }

        long endTime = System.currentTimeMillis();

        JsonObject resObject = new JsonObject();
        resObject.addProperty("name", DEBUG_RESOURCE);
        resObject.addProperty("time", (endTime - startTime) / 1000.0);
        resObject.add("result", md.writeJSON());

        return gson.toJson(resObject);
    }
}
