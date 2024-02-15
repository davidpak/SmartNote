package com.smartnote.server.export;

import static com.smartnote.server.util.JSONUtil.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartnote.server.format.notion.NotionConverter;
import com.smartnote.server.util.MIME;

/**
 * <p>
 * Interface to the Notion API. Provides utilities for interacting with the Notion
 * API, such as creating and modifying pages as well as OAuth and authentication.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionConverter
 */
public class NotionAPI {
    /**
     * Base URL for the Notion API.
     */
    public static final String NOTION_API_URL = "https://api.notion.com/v1/";

    /**
     * Notion API version. Used as the default version if none is provided.
     */
    public static final String NOTION_VERSION = "2022-06-28";

    /**
     * Base class for the result of a request to the Notion API.
     * 
     * @author Ethan Vrhel
     */
    public static abstract class NotionResult {
        public final int status;
        public final String message;

        private NotionResult(int status, JsonObject json) {
            this.status = status;
            this.message = getStringOrNull(json, "message");
        }

        /**
         * Check if the request was successful.
         * 
         * @return <code>true</code> if the request was successful.
         */
        public boolean success() {
            return status == 200;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    /**
     * Describes the result of creating a Notion API token.
     * 
     * @author Ethan Vrhel
     * @see NotionAPI#createToken(String, String)
     */
    public static final class CreateTokenResult extends NotionResult {
        public final String token;
        public final String botId;
        public final String duplicatedTemplateId;
        public final String workspaceIcon;
        public final String workspaceId;
        public final String workspaceName;
        
        private CreateTokenResult(int status, JsonObject json) {
            super(status, json);
            this.token = getStringOrNull(json, "access_token");
            this.botId = getStringOrNull(json, "bot_id");
            this.duplicatedTemplateId = getStringOrNull(json, "duplicated_template_id");
            this.workspaceIcon = getStringOrNull(json, "workspace_icon");
            this.workspaceId = getStringOrNull(json, "workspace_id");
            this.workspaceName = getStringOrNull(json, "workspace_name");
        }
    }

    /**
     * Describes the result of querying the pages available to the integration.
     * 
     * @author Ethan Vrhel
     * @see NotionAPI#queryAvailablePages()
     */
    public static final class QueryPagesResult extends NotionResult {
        public final List<Page> pages;

        private QueryPagesResult(int status, JsonObject json) {
            super(status, json);
            List<Page> pages = new ArrayList<>();
            JsonArray results = json.getAsJsonArray("results");
            for (JsonElement result : results) {
                JsonObject resultObject = result.getAsJsonObject();
                String id = resultObject.get("id").getAsString();
                String name = resultObject.getAsJsonObject("properties")
                        .getAsJsonObject("title")
                        .getAsJsonArray("title")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("text")
                        .get("content")
                        .getAsString();
                pages.add(new Page(id, name));
            }

            this.pages = Collections.unmodifiableList(pages);
        }
    }

    /**
     * Describes the result of creating a Notion page.
     * 
     * @author Ethan Vrhel
     * @see NotionAPI#createPage(String, String, JsonObject)
     */
    public static final class CreatePageResult extends NotionResult {
        public final String id;
        public final String url;

        private CreatePageResult(int status, JsonObject json) {
            super(status, json);
            this.id = getStringOrNull(json, "id");
            this.url = getStringOrNull(json, "url");
        }
    }
    
    /**
     * Describes the result of appending a block to a Notion page.
     * 
     * @author Ethan Vrhel
     * @see NotionAPI#appendBlock(String, JsonObject)
     */
    public static final class AppendBlockResult extends NotionResult {
        private AppendBlockResult(int status, JsonObject json) {
            super(status, json);
        }   
    }

    /**
     * Describes a Notion page.
     * 
     * @author Ethan Vrhel
     * @see NotionAPI#queryAvailablePages()
     */
    public static final record Page(String id, String name) { }

    /**
     * Convert a Notion endpoint to a URI.
     * 
     * @param endpoint The endpoint.
     * @return The URI.
     */
    public static URI uriOf(String endpoint) {
        return URI.create(NOTION_API_URL + endpoint);
    }

    private String version; // Notion API version
    private String token; // OAuth token (same as secret if internal integration)

    private Gson gson;

    private HttpClient client;

    /**
     * <p>Sets up the Notion API. To fully use the API, one must call
     * <code>authenticate</code> with a valid access token.</p>
     * 
     * @param version     The Notion API version. If <code>null</code>, the default
     *                    version is used.
     * 
     * @return <code>this</code>
     * @throws IOException If a HTTP client cannot be created.
     * @throws InterruptedException If the request is interrupted.
     * @throws IllegalStateException If the Notion API has already been initialized.
     */
    public NotionAPI build(String version)
            throws IOException, InterruptedException, IllegalStateException {
        if (this.client != null)
            throw new IllegalStateException("Notion API already initialized");

        this.version = version == null ? NOTION_VERSION : version;
        this.token = null;
        this.gson = new Gson();

        try {
            this.client = HttpClient.newHttpClient();
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }

        return this;
    }

    /**
     * <p>Set the integration token to use with Notion API requests. The token can
     * be obtained in the following ways:</p>
     * 
     * <ul>
     * <li>Using <code>createToken</code> with the code given by the Notion API at
     * the end of the OAuth flow.</li>
     * <li>Providing a token from a previous session.</li>
     * <li>The integration secret, if the integration is internal.</li>
     * </ul>
     * 
     * <p>This does not check the validity of the token.</p>
     * 
     * @param token The token.
     * @return <code>this</code>
     * @throws IllegalStateException If the Notion API has not been initialize or
     * the token has already been set.
     */
    public NotionAPI authenticate(String token) throws IllegalStateException {
        if (this.client == null)
            throw new IllegalStateException("Notion API has not been initialized");

        if (this.token != null)
            throw new IllegalStateException("Integration token already set");

        this.token = token;
        return this;
    }

    /**
     * <p>Create an access token to authenticate requests. Does not set the token in this
     * object. Use <code>authenticate</code> to set the token using the <code>token</code>
     * field of the result.</p>
     * 
     * @param clientId    The OAuth client ID.
     * @param secret      The OAuth client secret.
     * @param code        The code returned by the OAuth flow in the redirect URI.
     * @param redirectUri The redirect URI used in the OAuth flow.
     * @return The result of creating the token.
     * @throws IOException If the token cannot be obtained.
     * @throws InterruptedException If the request is interrupted.
     * @throws IllegalStateException If the Notion API has not been initialized
     * or any of the parameters are <code>null</code>.
     */
    public CreateTokenResult createToken(String clientId, String secret, String code, String redirectUri) throws IOException, InterruptedException, IllegalStateException {
        if (this.client == null)
            throw new IllegalStateException("Notion API has not been initialized");
        
        if (clientId == null)
            throw new IllegalStateException("Must provide an OAuth client ID");

        if (secret == null)
            throw new IllegalArgumentException("Must provide an OAuth client secret");

        if (code == null)
            throw new IllegalArgumentException("Must provide an OAuth code");

        if (redirectUri == null)
            throw new IllegalArgumentException("Must provide an OAuth redirect URI");

        JsonObject oauthObject = new JsonObject();
        oauthObject.addProperty("grant_type", "authorization_code");
        oauthObject.addProperty("code", code);
        oauthObject.addProperty("redirect_uri", redirectUri);

        String authorization = clientId + ":" + secret;

        // Base64 encode the client ID and secret
        String encoded = new String(Base64.getEncoder().encode(authorization.getBytes()));

        // Make a request to the Notion API to get the OAuth token
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uriOf("oauth/token"))
                .header("Content-Type", MIME.JSON)
                .header("Authorization", "Basic " + encoded)
                .header("Notion-Version", version)
                .POST(jsonPublisher(oauthObject));

        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);

        if (response.statusCode() != 200)
            throw new IOException("Failed to get access token: " + responseJson.get("error").getAsString());

        return new CreateTokenResult(response.statusCode(), responseJson);
    }
    
    /**
     * <p>Query the pages available to the integration. The pages are returned
     * in order of most recently created to least recently created.</p>
     * 
     * @return A list of pages available to the integration.
     * @throws IOException If the pages cannot be obtained.
     * @throws InterruptedException If the request is interrupted.
     * @throws IllegalStateException If the Notion API has not been initialized
     * or no authentication token has been set.
     */
    public QueryPagesResult queryAvailablePages() throws IOException, InterruptedException, IllegalStateException {
        if (this.client == null)
            throw new IllegalStateException("Notion API has not been initialized");

        if (this.token == null)
            throw new IllegalStateException("Not authenticated");

        JsonObject searchJsonObject = new JsonObject();

        JsonObject filterObject = new JsonObject();
        filterObject.addProperty("property", "object");
        filterObject.addProperty("value", "page");

        searchJsonObject.add("filter", filterObject);

        JsonObject sortJsonObject = new JsonObject();
        sortJsonObject.addProperty("direction", "descending");
        sortJsonObject.addProperty("timestamp", "created_time");

        // Make a request to the Notion API to get the available pages
        HttpRequest request = post("search", searchJsonObject).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new QueryPagesResult(response.statusCode(), responseJson(response));
    }

    /**
     * Create a Notion page as a subpage of another page.
     * 
     * @param name The name of the page to create.
     * @param pageId The ID of the page to create the new page under.
     * @param json The JSON object representing the page.
     * @return The result of creating the page.
     * @throws IOException If the page cannot be created.
     * @throws InterruptedException If the request is interrupted.
     * @throws IllegalStateException If the Notion API has not been initialized or
     * no authentication token has been set.
     */
    public CreatePageResult createPage(String name, String pageId, JsonObject json) throws IOException, InterruptedException, IllegalStateException {
        if (this.client == null)
            throw new IllegalStateException("Notion API has not been initialized");

        if (this.token == null)
            throw new IllegalStateException("Not authenticated");
            
        // Parent
        JsonObject parent = new JsonObject();
        parent.addProperty("type", "page_id");
        parent.addProperty("page_id", pageId);
        json.add("parent", parent);

        // Title
        JsonObject properties = new JsonObject();
        JsonArray titleArray = new JsonArray();
        JsonObject textObject = new JsonObject();
        JsonObject textDataObject = new JsonObject();
        textDataObject.addProperty("content", name);
        textObject.add("text", textDataObject);
        titleArray.add(textObject);
        properties.add("title", titleArray);
        json.add("properties", properties);

        HttpRequest request = post("pages", json).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new CreatePageResult(response.statusCode(), responseJson(response));
    }

    /**
     * Append a block to a Notion page.
     * 
     * @param blockId The ID of the block to append to.
     * @param json    The JSON object representing the block to append.
     * @return The status code of the request.
     * @throws IOException If the block cannot be appended.
     * @throws InterruptedException If the request is interrupted.
     * @throws IllegalStateException If the Notion API has not been initialized or
     * no authentication token has been set.
     */
    public AppendBlockResult appendBlock(String blockId, JsonObject json) throws IOException, InterruptedException, IllegalStateException {
        if (token == null)
            throw new IllegalStateException("Not authenticated");

        HttpRequest request = patch("blocks/" + formatId(blockId) + "/children", json).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new AppendBlockResult(response.statusCode(), responseJson(response));
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

    /**
     * Parse a response into a JSON object.
     * 
     * @param response The response.
     * @return The JSON object.
     */
    private JsonObject responseJson(HttpResponse<String> response) {
        return gson.fromJson(response.body(), JsonObject.class);
    }
}
