package com.smartnote.server.api.v1;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.export.ExportException;
import com.smartnote.server.export.ExportServiceConnectionException;
import com.smartnote.server.export.ExportServiceTimeoutException;
import com.smartnote.server.export.ExportServiceUnavailableException;
import com.smartnote.server.export.Exporter;
import com.smartnote.server.export.Exporters;
import com.smartnote.server.export.MalformedExportOptionsException;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
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
@ServerRoute(method = MethodType.POST, path = "/api/v1/export")
public class Export implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type("application/json");

        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();

        Session session = Server.getServer().getSessionManager().getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\":\"No session\"}";
        }
        
        String body = request.body();
        if (body == null) {
            response.status(400);
            return "{\"message\":\"Missing export options\"}";
        }

        Gson gson = new Gson();
        JsonObject options;
        try {
            options = gson.fromJson(body, JsonObject.class);
        } catch (Exception e) {
            response.status(400);
            return "{\"message\":\"Malformed export options\"}";
        }

        JsonElement type = options.get("type");
        if (type == null) {
            response.status(400);
            return "{\"message\":\"Missing export type\"}";
        }

        if (!type.isJsonPrimitive()) {
            response.status(400);
            return "{\"message\":\"Invalid export type\"}";
        }

        JsonPrimitive typePrimitive = type.getAsJsonPrimitive();
        if (!typePrimitive.isString()) {
            response.status(400);
            return "{\"message\":\"Invalid export type\"}";
        }

        JsonElement toExportElement = options.get("name");
        if (toExportElement == null) {
            response.status(400);
            return "{\"message\":\"Missing resource name\"}";
        }

        if (!toExportElement.isJsonPrimitive()) {
            response.status(400);
            return "{\"message\":\"Invalid resource name\"}";
        }

        JsonPrimitive toExportPrimitive = toExportElement.getAsJsonPrimitive();
        if (!toExportPrimitive.isString()) {
            response.status(400);
            return "{\"message\":\"Invalid resource name\"}";
        }

        String toExport = toExportPrimitive.getAsString();

        // Find exporter for requested type
        String typeString = typePrimitive.getAsString();
        Exporter exporter = Exporters.getExporters().getExporter(typeString);
        if (exporter == null) {
            response.status(400);
            return "{\"message\":\"Invalid export type\"}";
        }

        // Export the resource
        JsonObject result;
        try {
            Resource resource = resourceSystem.findResource(toExport, session.getPermission());
            String data = resource.readToString();
            result = exporter.export(data, options, session.getPermission());
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\":\"Access denied\"}";
        } catch (InvalidPathException e) {
            response.status(400);
            return "{\"message\":\"Invalid path\"}";
        } catch (NoSuchResourceException e) {
            response.status(404);
            return "{\"message\":\"Resource not found\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\":\"Export service had IO error\"}";
        } catch (ExportServiceConnectionException e) {
            response.status(502);
            return "{\"message\":\"Could not connect to export service\"}";
        } catch (ExportServiceUnavailableException e) {
            response.status(503);
            return "{\"message\":\"Export service unavailable\"}";
        } catch (ExportServiceTimeoutException e) {
            response.status(504);
            return "{\"message\":\"Export service timed out\"}";
        } catch (MalformedExportOptionsException e) {
            return "{\"message\":\"" + e.getMessage() + "\"}";
        } catch (ExportException e) {
            response.status(500);
            return "{\"message\":\"Export error\"}";
        }
        
        if (!result.has("message"))
            result.addProperty("message", "Export successful");

        response.status(200);
        return gson.toJson(result);
    }
}
