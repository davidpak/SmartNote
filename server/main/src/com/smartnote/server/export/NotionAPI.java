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
import com.google.gson.JsonArray;
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
            System.err.println("Usage: NotionAPI <secret> <code>");
            System.exit(1);
        }

        String markdown = Files.readString(Paths.get("private", "output.md"));

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        NotionRenderer renderer = new NotionRenderer();
        JsonObject notionJson = renderer.renderJson(document);

        String secret = args[0];
        String code = args[1];
        String version = "2022-06-28";

        NotionAPI api = new NotionAPI().build(secret, version, code);
        int rc = api.create("Test Page", notionJson);
        System.out.println("Status code: " + rc);
    }

    private String secret; // Integration secret
    private String token; // OAuth token
    private String version;

    private Gson gson;

    private HttpClient client;

    /**
     * Sets up the Notion API.
     * 
     * @param secret      The integration secret.
     * @param version     The Notion API version.
     * @param code        The code for the Notion API, as returned by the OAuth
     *                    process in the <code>code</code> query parameter.
     * @param redirectUri The redirect URI for the Notion API, as returned by the
     *                    OAuth process in the <code>redirect_uri</code> query
     *                    parameter.
     * @return <code>this</code>
     * @throws IOException If the system does not have the necessary
     *                     resources to connect to the Notion API.
     */
    public NotionAPI build(String secret, String version, String code, String redirectUri)
            throws IOException, InterruptedException {
        this.secret = secret;
        this.version = version;
        this.gson = new Gson();
        try {
            this.client = HttpClient.newHttpClient();
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }

        JsonObject oauthObject = new JsonObject();
        oauthObject.addProperty("grant_type", "authorization_code");
        oauthObject.addProperty("code", code);
        oauthObject.addProperty("redirect_uri", redirectUri);

        // Make a request to the Notion API to get the OAuth token
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uriOf("oauth/token"))
                .header("Content-Type", MIME.JSON)
                .header("Authorization", "Basic " + secret)
                .POST(jsonPublisher(oauthObject));

        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new IOException("Failed to get OAuth token: " + response.body());

        return this;
    }

    /**
     * Create a Notion page in the workspace.
     * 
     * @param name The name of the page.
     * @param json The JSON object representing the page.
     * @throws IOException          If the system does not have the necessary
     *                              resources to connect to the Notion API.
     * @throws InterruptedException If the request is interrupted.
     */
    public int create(String name, JsonObject json) throws IOException, InterruptedException {
        JsonObject parent = new JsonObject();
        parent.addProperty("type", "workspace");
        parent.addProperty("workspace", true);

        JsonObject properties = new JsonObject();
        JsonObject nameObject = new JsonObject();
        JsonArray titleArray = new JsonArray();
        JsonObject textObject = new JsonObject();
        textObject.addProperty("content", name);
        titleArray.add(textObject);
        nameObject.add("title", titleArray);
        properties.add("Name", nameObject);

        json.add("parent", parent);
        json.add("properties", properties);

        HttpRequest request = post("pages", json).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    /**
     * Append a block to a Notion page.
     * 
     * @param blockId The ID of the block to append to.
     * @param json    The JSON object representing the block to append.
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
     * @param body     The JSON body.
     * @return The new POST request builder.
     */
    private HttpRequest.Builder post(String endpoint, JsonObject body) {
        return auth(to(endpoint).method("POST", jsonPublisher(body))).header("Content-Type", MIME.JSON);
    }

    /**
     * Build a new PATCH request builder with the given endpoint and JSON body.
     * 
     * @param endpoint The endpoint.
     * @param body     The JSON body.
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
