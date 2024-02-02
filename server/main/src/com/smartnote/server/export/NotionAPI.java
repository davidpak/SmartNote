package com.smartnote.server.export;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class NotionAPI {
    public static final String NOTION_API_URL = "https://api.notion.com/v1";
    
    public static URI getURIFor(String path) {
        return URI.create(NOTION_API_URL + path);
    }

    private String token;
    private String version;

    private Gson gson;

    private HttpClient client;

    private JsonObject databases;

    public NotionAPI build(String token, String version) throws IOException, InterruptedException, Exception {
        this.token = token;
        this.version = version;
        this.gson = new Gson();
        this.client = HttpClient.newHttpClient();
        this.databases = null;

        URI databasesURI = getURIFor("/databases");

        HttpRequest request = HttpRequest.newBuilder(databasesURI)
            .header("Authorization", "Bearer " + token)
            .header("Notion-Version", version)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new Exception("Failed to build Notion API: " + response.statusCode());

        this.databases = gson.fromJson(response.body(), JsonObject.class);

        return this;
    }

    public JsonObject getDatabases() {
        return databases;
    }

    public JsonObject createPage(String databaseID, JsonObject properties) throws IOException, InterruptedException {
        URI createPageURI = getURIFor("/pages");

        JsonObject body = new JsonObject();
        body.addProperty("parent", databaseID);
        body.add("properties", properties);

        HttpRequest request = HttpRequest.newBuilder(createPageURI)
            .header("Authorization", "Bearer " + token)
            .header("Notion-Version", version)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new IOException("Failed to create page: " + response.body());

        return gson.fromJson(response.body(), JsonObject.class);
    }
}
