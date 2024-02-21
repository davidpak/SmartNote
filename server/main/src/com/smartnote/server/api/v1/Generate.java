package com.smartnote.server.api.v1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.GeneratorConfig;
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
 * <p>
 * Generates summaries from uploaded files.
 * </p>
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
        GeneratorConfig generatorConfig = Server.getServer().getConfig().getGeneratorConfig();
        String summarizer = generatorConfig.getSummarizer();

        long startTime = System.currentTimeMillis();

        Session session = sessionManager.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\":\"No session\"}";
        }

        Permission permission = session.getPermission();

        String resourceName;
        if (generatorConfig.isDebug()) {
            resourceName = generatorConfig.getDebugResource();
        } else {
            try {
                resourceName = generate(summarizer, request.body(), session, permission);
            } catch (IllegalArgumentException e) {
                response.status(400);
                return "{\"message\":" + e.getMessage() + "\"}";
            } catch (NoSuchResourceException e) {
                response.status(404);
                return "{\"message\":\"Resource not found\"}";
            } catch (SecurityException e) {
                response.status(403);
                return "{\"message\":\"Permission denied\"}";
            } catch (IOException e) {
                response.status(500);
                return "{\"message\":\"Generation failed\"}";
            } catch (InterruptedException e) {
                response.status(500);
                return "{\"message\":\"Data generation interrupted\"}";
            }
        }

        Resource resource;

        try {
            resource = resourceSystem.findResource(resourceName, permission);
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
        resObject.addProperty("name", resourceName);
        resObject.addProperty("time", (endTime - startTime) / 1000.0);
        resObject.add("result", md.writeJSON());

        return new Gson().toJson(resObject);
    }

    private String generate(String summarizer, String body, Session session,
            Permission permission)
            throws IllegalArgumentException, NoSuchResourceException, SecurityException, IOException,
            InterruptedException {
        JsonObject generateJson = new Gson().fromJson(body, JsonObject.class);

        JsonObject generalOptions = getObjectOrNull(generateJson, "general");
        if (generalOptions == null)
            throw new IllegalArgumentException("Missing field general");

        JsonArray files = getArrayOrNull(generalOptions, "files");
        if (files == null)
            throw new IllegalArgumentException("Missing field general.files");

        if (files.size() == 0)
            throw new IllegalArgumentException("No files specified");

        JsonObject llmOptions = getObjectOrNull(generateJson, "llm");
        if (llmOptions == null)
            throw new IllegalArgumentException("Missing field llm");

        double verbosity = getNumberOrDefault(llmOptions, "verbosity", 0.5);

        String first = files.get(0).getAsString();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(first, permission);

        String path = resource.getPath().toString();

        ProcessBuilder pb = new ProcessBuilder("python3", summarizer, path);
        Process p = pb.start();

        int exitCode = p.waitFor();

        if (exitCode != 0)
            throw new IOException("Summarizer exited with non-zero exit code");

        return null;
    }
}
