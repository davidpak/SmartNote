package com.smartnote.server.export;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
    public static final String NOTION_VERSION = "2021-08-16";

    @Override
    public JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException,
            InvalidPathException, IOException, ExportException, MalformedExportOptionsException {
        String token = getToken(options);
        NotionAPI notionAPI = new NotionAPI();

        JsonObject result;
        try {
            notionAPI.build(token, NOTION_VERSION);

            result = new JsonObject();
        } catch (IOException e) {
            throw new ExportServiceUnavailableException(e);
        } catch (Exception e) {
            throw new ExportServiceConnectionException(e);
        }

        return result;
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
}
