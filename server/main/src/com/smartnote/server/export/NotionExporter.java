package com.smartnote.server.export;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.format.notion.NotionRenderer;

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

        Parser parser = Parser.builder().build();
        Node document = parser.parse(data);

        NotionRenderer renderer = new NotionRenderer();
        JsonObject json = renderer.renderJson(document);

        try {
            notionAPI.build(token, NOTION_VERSION);
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
