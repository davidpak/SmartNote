package com.smartnote.server.export;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import com.google.gson.JsonObject;

@ExporterInfo(name = "notion")
public class NotionExporter implements Exporter {
    
    @Override
    public JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException, InvalidPathException, IOException, ExportException {
        throw new ExportServiceUnavailableException();
    }
}
