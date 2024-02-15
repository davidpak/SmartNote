package com.smartnote.server.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.Permission;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.Server;
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
    public static final String NOTION_VERSION = "2022-06-28";

    @Override
    public JsonObject export(ExportOptions options, Permission permission) throws SecurityException,
            InvalidPathException, IOException, ExportException, MalformedExportOptionsException {

        String parentPage = getParentPage(options.getRemote());
        String code = getCode(options.getRemote());
        String secret = getSecret(options.getRemote()); // TODO: Use secret
        String redirectUri = getRedirectUri(options.getRemote());
        String token = getToken(options.getRemote());
        NotionAPI notionAPI = new NotionAPI();

        ParsedMarkdown md = ParsedMarkdown.parse(options.readInputData(permission));
        NotionConverter notionConverter = new NotionConverter();
        NotionBlock block = notionConverter.convert(md);
        JsonObject json = block.writeJSON();

        NotionConfig config = Server.getServer().getConfig().getNotionConfig();

        if (secret == null)
            secret = config.getSecret();

        try {
            notionAPI.build(config.getClientId(), secret, NOTION_VERSION);
            
            if (token == null)
                readToken(notionAPI, permission, code, redirectUri);
            else
                notionAPI.oauth(token);
           
            int rc = notionAPI.createPage(parentPage, json);
            if (rc != 200)
                throw new ExportServiceUnavailableException("Notion API returned " + rc + " status code");

        } catch (IOException e) {
            throw new ExportServiceUnavailableException(e);
        } catch (Exception e) {
            throw new ExportServiceConnectionException(e);
        }

        return new JsonObject();
    }

    private static String getParentPage(JsonObject remote) throws MalformedExportOptionsException {
        JsonElement parentPageElement = remote.get("parent");
        if (parentPageElement == null)
            throw new MalformedExportOptionsException("No parent provided");

        JsonPrimitive parentPagePrimitive = parentPageElement.getAsJsonPrimitive();
        if (!parentPagePrimitive.isString())
            throw new MalformedExportOptionsException("parent is not a string");

        return parentPagePrimitive.getAsString();
    }

    private static String getCode(JsonObject remote) throws MalformedExportOptionsException {
        JsonElement codeElement = remote.get("code");
        if (codeElement == null)
            return null;

        JsonPrimitive codePrimitive = codeElement.getAsJsonPrimitive();
        if (!codePrimitive.isString())
            return null;

        return codePrimitive.getAsString();
    }

    private static String getSecret(JsonObject remote) throws MalformedExportOptionsException {
        JsonElement secretElement = remote.get("secret");
        if (secretElement == null)
            return null;

        JsonPrimitive secretPrimitive = secretElement.getAsJsonPrimitive();
        if (!secretPrimitive.isString())
            return null;

        return secretPrimitive.getAsString();
    }

    private static String getRedirectUri(JsonObject remote) throws MalformedExportOptionsException {
        JsonElement redirectUriElement = remote.get("redirectUri");
        if (redirectUriElement == null)
            return null;

        JsonPrimitive redirectUriPrimitive = redirectUriElement.getAsJsonPrimitive();
        if (!redirectUriPrimitive.isString())
            return null;

        return redirectUriPrimitive.getAsString();
    }

    private static String getToken(JsonObject remote) throws MalformedExportOptionsException {
        JsonElement tokenElement = remote.get("token");
        if (tokenElement == null)
            return null;

        JsonPrimitive tokenPrimitive = tokenElement.getAsJsonPrimitive();
        if (!tokenPrimitive.isString())
            return null;

        return tokenPrimitive.getAsString();
    }

    private static void readToken(NotionAPI notion, Permission permission, String code, String redirectUri) throws IOException, InterruptedException {
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        
        String token = null;

        // read token
        try {
            Resource tokenResource = resourceSystem.findActualResource("session", Paths.get(".notion_token"), permission);
            InputStream in = tokenResource.openInputStream();
            token = new String(in.readAllBytes());
            in.close();
        } catch (Exception e) {
            // Ignore
        }

        if (token != null) {
            notion.oauth(token);
            return;
        }


        if (code == null)
            throw new IllegalArgumentException("No code or token provided");

        token = notion.createToken(code, redirectUri);

        // save token
        try {
            Resource tokenResource = resourceSystem.findActualResource("session", Paths.get(".notion_token"), permission);
            OutputStream out = tokenResource.openOutputStream();
            out.write(token.getBytes());
            out.close();
        } catch (Exception e) {
            // Ignore
        }
    }
}
