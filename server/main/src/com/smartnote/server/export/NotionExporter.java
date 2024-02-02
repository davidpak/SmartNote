package com.smartnote.server.export;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import com.google.gson.JsonObject;

/**
 * <p>Exports to Notion using the Notion API.</p>
 * 
 * @author Ethan Vrhel
 * @see RemoteExporter
 */
@ExporterInfo(name = "notion")
public class NotionExporter implements RemoteExporter {  
    @Override
    public JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException, InvalidPathException, IOException, ExportException {
        throw new ExportServiceUnavailableException();
    }
}
