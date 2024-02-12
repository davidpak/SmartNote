package com.smartnote.server.export;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * <p>
 * Interface to the Notion API.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionExporter
 */
public class NotionAPI {
    /**
     * Base URL for the Notion API.
     */
    public static final String NOTION_API_URL = "https://api.notion.com/v1";

    public static URI uriOf(String endpoint) {
        return URI.create(NOTION_API_URL + endpoint);
    }

    private String token;
    private String version;

    private Gson gson;

    private HttpClient client;

    /**
     * Sets up the Notion API.
     * 
     * @param token   The Notion API token.
     * @param version The Notion API version.
     * @return <code>this</code>
     * @throws IOException If the system does not have the necessary
     *                     resources to connect to the Notion API.
     */
    public NotionAPI build(String token, String version) throws IOException {
        this.token = token;
        this.version = version;
        this.gson = new Gson();
        try {
            this.client = HttpClient.newHttpClient();
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }

        return this;
    }

    public void create(JsonObject pageJson) throws IOException {

    }

    private void append(String blockId, String jsonString) {

    }

    private HttpRequest.Builder append(String blockId, JsonObject body) {
        String endpoint = "blocks/" + formatId(blockId) + "/children";
        return auth(to(endpoint).method("PATCH", jsonPublisher(body)));
    }

    private HttpRequest.Builder auth(HttpRequest.Builder b) {
        return b.header("Authorization", "Bearer " + token).header("Notion-Version", version);
    }

    /**
     * Format a Notion ID to the dashed format. If the ID is already in the dashed
     * format, it is returned as is.
     * 
     * @param id The ID to format.
     * @return The formatted ID.
     */
    private static String formatId(String id) {
        if (id.length() == 36)
            return id;

        StringBuilder sb = new StringBuilder(id);
        sb.insert(8, '-');
        sb.insert(13, '-');
        sb.insert(18, '-');
        sb.insert(23, '-');

        return sb.toString();
    }

    private static HttpRequest.Builder to(String endpoint) {
        return HttpRequest.newBuilder().uri(uriOf(endpoint));
    }

    private static BodyPublisher jsonPublisher(JsonElement json) {
        return HttpRequest.BodyPublishers.ofString(new Gson().toJson(json));
    }
}
