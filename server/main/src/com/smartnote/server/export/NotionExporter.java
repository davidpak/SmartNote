package com.smartnote.server.export;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.Server;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.notion.NotionBlock;
import com.smartnote.server.format.notion.NotionConverter;

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
    public JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException,
            InvalidPathException, IOException, ExportException, MalformedExportOptionsException {
        String token = getToken(options);
        String pageId = getPageId(options);
        NotionAPI notionAPI = new NotionAPI();

        ParsedMarkdown md = ParsedMarkdown.parse(data);
        NotionConverter notionConverter = new NotionConverter();
        NotionBlock block = notionConverter.convert(md);
        JsonObject json = block.writeJSON();

        NotionConfig config = Server.getServer().getConfig().getNotionConfig();

        try {
            notionAPI.build(config.getClientId(), config.getSecret(), NOTION_VERSION);
            int rc = notionAPI.appendBlock(pageId, json);
            if (rc != 200)
                throw new ExportServiceUnavailableException("Notion API returned " + rc + " status code");

        } catch (IOException e) {
            throw new ExportServiceUnavailableException(e);
        } catch (Exception e) {
            throw new ExportServiceConnectionException(e);
        }

        return new JsonObject();
    }

    private static String getToken(JsonObject options) throws MalformedExportOptionsException {
        JsonElement tokenElement = options.get("token");
        if (tokenElement == null)
            throw new MalformedExportOptionsException("No token provided");

        JsonPrimitive tokenPrimitive = tokenElement.getAsJsonPrimitive();
        if (!tokenPrimitive.isString())
            throw new MalformedExportOptionsException("Token is not a string");

        return tokenPrimitive.getAsString();
    }

    public static String getPageId(JsonObject options) throws MalformedExportOptionsException {
        JsonElement pageIdElement = options.get("pageId");
        if (pageIdElement == null)
            throw new MalformedExportOptionsException("No pageId provided");

        JsonPrimitive pageIdPrimitive = pageIdElement.getAsJsonPrimitive();
        if (!pageIdPrimitive.isString())
            throw new MalformedExportOptionsException("PageId is not a string");

        return pageIdPrimitive.getAsString();
    }
}
