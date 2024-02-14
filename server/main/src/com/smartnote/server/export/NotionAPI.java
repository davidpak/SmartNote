package com.smartnote.server.export;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartnote.server.format.notion.NotionRenderer;
import com.smartnote.server.util.MIME;

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
    public static final String NOTION_API_URL = "https://api.notion.com/v1/";

    public static URI uriOf(String endpoint) {
        return URI.create(NOTION_API_URL + endpoint);
    }

    // For testing
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: NotionAPI <token> <blockId>");
            System.exit(1);
        }

        String markdown = Files.readString(Paths.get("private", "output.md"));

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        NotionRenderer renderer = new NotionRenderer();
        JsonObject notionJson = renderer.renderJson(document);

        String token = args[0];
        String blockId = args[1];
        String version = "2022-06-28";

        NotionAPI api = new NotionAPI().build(token, version);
        int rc = api.appendBlock(blockId, notionJson);
        System.out.println("Status code: " + rc);
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

    /**
     * Create a Notion page.
     * 
     * @param parentId The ID of the parent page.
     * @param name The name of the page.
     * @param json The JSON object representing the page.
     * @throws IOException          If the system does not have the necessary
     *                              resources to connect to the Notion API.
     * @throws InterruptedException If the request is interrupted.
     */
    public void create(String parentId, String name, JsonObject json) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Append a block to a Notion page.
     * 
     * @param blockId The ID of the block to append to.
     * @param json   The JSON object representing the block to append.
     * @return The status code of the request.
     * @throws IOException          If the system does not have the necessary
     *                              resources to connect to the Notion API.
     * @throws InterruptedException If the request is interrupted.
     */
    public int appendBlock(String blockId, JsonObject json) throws IOException, InterruptedException {
        HttpRequest request = patch("blocks/" + formatId(blockId) + "/children", json).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    
    /**
     * Build a new POST request builder with the given endpoint and JSON body.
     * 
     * @param endpoint The endpoint.
     * @param body   The JSON body.
     * @return The new POST request builder.
     */
    private HttpRequest.Builder post(String endpoint, JsonObject body) {
        return auth(to(endpoint).method("POST", jsonPublisher(body))).header("Content-Type", MIME.JSON);
    }

    /**
     * Build a new PATCH request builder with the given endpoint and JSON body.
     * 
     * @param endpoint The endpoint.
     * @param body    The JSON body.
     * @return The new PATCH request builder.
     */
    private HttpRequest.Builder patch(String endpoint, JsonObject body) {
        return auth(to(endpoint).method("PATCH", jsonPublisher(body))).header("Content-Type", MIME.JSON);
    }

    /**
     * Append the Notion API token and version to a request builder.
     * 
     * @param b The request builder.
     * @return The request builder with the Notion API token and version appended.
     */
    private HttpRequest.Builder auth(HttpRequest.Builder b) {
        return b.header("Authorization", "Bearer " + token).header("Notion-Version", version);
    }

    /**
     * Create a new HTTP request body publisher with the given JSON element.
     * 
     * @param json The JSON element.
     * @return The new HTTP request body publisher.
     */
    private BodyPublisher jsonPublisher(JsonElement json) {
        return HttpRequest.BodyPublishers.ofString(gson.toJson(json));
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

    /**
     * Create a new HTTP request builder with the given Notion endpoint.
     * 
     * @param endpoint The endpoint.
     * @return The new HTTP request builder.
     */
    private static HttpRequest.Builder to(String endpoint) {
        return HttpRequest.newBuilder().uri(uriOf(endpoint));
    }
}
