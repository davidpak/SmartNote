package com.smartnote.server.export;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.Server;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;

import spark.Request;
import spark.Response;

public class ExportOptions {
    private String name;
    private String type;
    private String output;
    private JsonObject remote;

    private String data;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOutput() {
        return output;
    }

    public JsonObject getRemote() {
        return remote;
    }

    public String getData() {
        return data;
    }

    public String readInputData(Permission permission) throws InvalidPathException, NoSuchResourceException, SecurityException, IOException {
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(name, permission);
        InputStream in = resource.openInputStream();
        byte[] data = in.readAllBytes();
        in.close();
        this.data = new String(data);
        return this.data;
    }
    
    public String parse(Request request, Response response) {
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
            return "{\"message\":\"type must be a string\"}";
        }

        JsonPrimitive typePrimitive = type.getAsJsonPrimitive();
        if (!typePrimitive.isString()) {
            response.status(400);
            return "{\"message\":\"type must be a string\"}";
        }

        this.type = typePrimitive.getAsString();

        JsonElement toExportElement = options.get("name");
        if (toExportElement == null) {
            response.status(400);
            return "{\"message\":\"Missing resource name\"}";
        }

        if (!toExportElement.isJsonPrimitive()) {
            response.status(400);
            return "{\"message\":\"name must be a string\"}";
        }

        JsonPrimitive toExportPrimitive = toExportElement.getAsJsonPrimitive();
        if (!toExportPrimitive.isString()) {
            response.status(400);
            return "{\"message\":\"name must be a string\"}";
        }

        this.name = toExportPrimitive.getAsString();

        // output is optional
        JsonElement outputElement = options.get("output");
        if (outputElement != null && outputElement.isJsonPrimitive()) {
            JsonPrimitive outputPrimitive = outputElement.getAsJsonPrimitive();
            if (outputPrimitive.isString())
                this.output = outputPrimitive.getAsString();
        }

        // remote is optional
        JsonElement remoteElement = options.get("remote");
        if (remoteElement != null && remoteElement.isJsonObject())
            this.remote = remoteElement.getAsJsonObject();

        return null;
    }
}
