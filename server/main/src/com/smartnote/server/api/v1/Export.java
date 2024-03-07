package com.smartnote.server.api.v1;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.export.ExportException;
import com.smartnote.server.export.ExportOptions;
import com.smartnote.server.export.ExportServiceConnectionException;
import com.smartnote.server.export.ExportServiceTimeoutException;
import com.smartnote.server.export.ExportServiceUnavailableException;
import com.smartnote.server.export.Exporter;
import com.smartnote.server.export.MalformedExportOptionsException;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.util.MIME;
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
        response.type(MIME.JSON);

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
        } catch (JsonSyntaxException e) {
            response.status(400);
            return "{\"message\":\"Malformed export options\"}";
        }
  
        ExportOptions exportOptions = new ExportOptions();
        try {
            exportOptions.parse(options);
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "{\"message\":\"Missing or invalid export option: " + e.getMessage() + "\"}";
        } catch (NoSuchElementException e) {
            response.status(400);
            return "{\"message\":\"Missing or invalid export option: " + e.getMessage() + "\"}";
        }

        Exporter exporter = exportOptions.getExporter();

        // Export the resource
        JsonObject result = new JsonObject();
        try {
            result = exporter.export(exportOptions, session.getPermission());
        } catch (SecurityException e) {
            response.status(403);
            result.addProperty("message", "Access denied");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (InvalidPathException e) {
            response.status(400);
            result.addProperty("message", "Invalid path");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (NoSuchResourceException e) {
            response.status(404);
            result.addProperty("message", "Resource not found");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (IOException e) {
            response.status(500);
            result.addProperty("message", "IO error");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (ExportServiceConnectionException e) {
            response.status(502);
            result.addProperty("message", "Could not connect to export service");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (ExportServiceUnavailableException e) {
            response.status(503);
            result.addProperty("message", "Export service unavailable");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (ExportServiceTimeoutException e) {
            response.status(504);
            result.addProperty("message", "Export service timed out");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (MalformedExportOptionsException e) {
            response.status(400);
            result.addProperty("message", "Malformed export options");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        } catch (ExportException e) {
            response.status(500);
            result.addProperty("message", "Export error");
            if (exportOptions.getExtended() != null)
                result.addProperty("extended", exportOptions.getExtended());
            return gson.toJson(result);
        }
        
        if (!result.has("message"))
            result.addProperty("message", "Export successful");

        if (exportOptions.getExtended() != null)
            result.addProperty("extended", exportOptions.getExtended());

        response.status(200);
        return gson.toJson(result);
    }
}
