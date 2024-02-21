package com.smartnote.server.export;

import static com.smartnote.server.util.JSONUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.export.NotionAPI.CreatePageResult;
import com.smartnote.server.export.NotionAPI.CreateTokenResult;
import com.smartnote.server.export.NotionAPI.NotionResult;
import com.smartnote.server.export.NotionAPI.QueryPagesResult;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.notion.NotionBlock;
import com.smartnote.server.format.notion.NotionConverter;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;

/**
 * <p>
 * Exports to Notion using the Notion API.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see RemoteExporter
 */
@ExporterInfo(name = "notion")
public class NotionExporter implements RemoteExporter {
    /**
     * The default page name.
     */
    public static final String DEFAULT_PAGE_NAME = "Exported Page";

    private static final Logger LOG = LoggerFactory.getLogger(NotionExporter.class);

    @Override
    public JsonObject export(ExportOptions options, Permission permission) throws SecurityException,
            InvalidPathException, IOException, ExportException, MalformedExportOptionsException {
        JsonObject response = new JsonObject();

        NotionExportOptions nopts = new NotionExportOptions();
        nopts.parse(options);
        nopts.load(options.readInputData(permission));

        try {
            NotionAPI notionAPI = nopts.createApi(permission);
            NotionResult result;

            if (nopts.mode.equalsIgnoreCase("new")) {
                // Create a new page
                if (nopts.page == null) {
                    QueryPagesResult paResult = notionAPI.queryAvailablePages();
                    if (!paResult.success()) {
                        LOG.info("Notion API error: " + paResult.message);
                        throw new ExportServiceUnavailableException(paResult.message);
                    }

                    if (paResult.pages.size() == 0) {
                        LOG.info("No pages available");
                        throw new ExportServiceUnavailableException("No pages available");
                    }

                    nopts.page = paResult.pages.get(0).id();
                }

                CreatePageResult pResult = notionAPI.createPage(nopts.pageName, nopts.page, nopts.json);
                response.addProperty("url", pResult.url);
                response.addProperty("id", pResult.id);
                result = pResult;
            } else if (nopts.mode.equalsIgnoreCase("update")) {
                // Replace the contents of a page
                if (nopts.page == null)
                    throw new MalformedExportOptionsException("No page provided for update");

                throw new UnsupportedOperationException("update not implemented");
            } else if (nopts.mode.equalsIgnoreCase("append")) {
                // Append to a page
                if (nopts.page == null)
                    throw new MalformedExportOptionsException("No page provided for append");

                // append only allows children
                JsonArray children = nopts.json.getAsJsonArray("children");
                JsonObject apppendObject = new JsonObject();
                apppendObject.add("children", children);

                result = notionAPI.appendBlock(nopts.page, apppendObject);
            } else {
                throw new MalformedExportOptionsException("Invalid mode: " + nopts.mode);
            }

            if (!result.success()) {
                LOG.info("Notion API error: " + result.message);
                throw new ExportServiceUnavailableException(result.message);
            }
        } catch (IOException e) {
            LOG.info("Notion API error: " + e.getMessage());
            throw new ExportServiceUnavailableException("Could not connect to Notion API: " + e.getMessage());
        } catch (InterruptedException e) {
            LOG.info("Notion API error: " + e.getMessage());
            throw new ExportServiceTimeoutException("Connection to Notion API timed out: " + e.getMessage());
        }

        response.addProperty("name", nopts.pageName);

        return response;
    }

    private static class NotionExportOptions {
        private String mode;
        private String page;
        private String code;
        private String redirectUri;

        // integration
        private String secret;
        private String clientId;
        private String token;

        private String pageName;
        private JsonObject json;

        public void parse(ExportOptions options) throws MalformedExportOptionsException {
            NotionConfig config = Server.getServer().getConfig().getNotionConfig();

            JsonObject json = options.getRemote();

            try {
                // basic
                mode = getStringOrException(json, "mode");
                page = getStringOrNull(json, "page");
                code = getStringOrNull(json, "code");
                redirectUri = getStringOrNull(json, "redirectUri");

                // integration
                JsonObject integration = getObjectOrNull(json, "integration");
                if (integration != null) {
                    if (!config.allowRemoteIntegrations())
                        throw new MalformedExportOptionsException("Remote integrations are not allowed");

                    secret = getStringOrNull(integration, "secret");
                    clientId = getStringOrNull(integration, "clientId");
                    token = getStringOrNull(integration, "token");

                    // Enforce integration rules
                    if (token == null && clientId == null && secret == null)
                        throw new MalformedExportOptionsException("Integration: No secret provided");
                    if (token != null && (clientId != null || secret != null))
                        throw new MalformedExportOptionsException(
                                "Integration: Token only valid without client ID or secret");
                    if (clientId != null && (secret == null || token != null))
                        throw new MalformedExportOptionsException("Integration: Secret not provided with client ID");
                } else {
                    secret = config.getSecret();
                    clientId = config.getClientId();
                }
            } catch (IllegalArgumentException e) {
                throw new MalformedExportOptionsException(e.getMessage());
            }

            pageName = options.getOutput();

            if (secret == null)
                secret = config.getSecret();
        }

        public void load(String inputData) {
            // Convert markdown to Notion JSON
            ParsedMarkdown md = ParsedMarkdown.parse(inputData);
            NotionConverter notionConverter = new NotionConverter();
            NotionBlock block = notionConverter.convert(md);
            json = block.writeJSON();

            if (pageName == null) {
                // set page name to first heading
                NotionBlock heading = block.findFirstOf("heading_1");
                if (heading != null)
                    pageName = heading.getPlainText();

                // default page name
                if (pageName == null)
                    pageName = DEFAULT_PAGE_NAME;
            }
        }

        public NotionAPI createApi(Permission permission)
                throws IOException, InterruptedException, ExportServiceUnavailableException {
            NotionAPI notionAPI = new NotionAPI();

            // initialize and load oauth token
            notionAPI.build(null);
            loadToken(notionAPI, permission);

            return notionAPI;
        }

        private void loadToken(NotionAPI notion, Permission permission)
                throws IOException, InterruptedException, ExportServiceUnavailableException {
            ResourceSystem resourceSystem = Server.getServer().getResourceSystem();

            if (token != null) {
                notion.authenticate(token);
                return;
            }

            // read token
            InputStream in = null;
            try {
                Resource tokenResource = resourceSystem.findActualResource("session", Paths.get(".notion_token"),
                        permission);
                in = tokenResource.openInputStream();
                token = new String(in.readAllBytes());
            } catch (Exception e) {
                // Ignore
            } finally {
                if (in != null)
                    in.close();
            }

            if (token != null) {
                notion.authenticate(token);
                return;
            }

            if (code == null)
                throw new IllegalArgumentException("No code or token provided");

            CreateTokenResult result = notion.createToken(clientId, secret, code, redirectUri);
            if (!result.success())
                throw new ExportServiceUnavailableException(result.message);

            token = result.token;

            notion.authenticate(token);

            // save token
            OutputStream out = null;
            try {
                Resource tokenResource = resourceSystem.findActualResource("session", Paths.get(".notion_token"),
                        permission);
                out = tokenResource.openOutputStream();
                out.write(token.getBytes());
            } catch (Exception e) {
                // Ignore
            } finally {
                if (out != null)
                    out.close();
            }
        }
    }
}
