package com.smartnote.server.export;

import java.security.Permission;

import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownConverter;

/**
 * <p>
 * Exports to JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see ResourceExporter
 */
@ExporterInfo(name = "json")
public class JSONExporter implements ResourceExporter {
    @Override
    public MarkdownConverter<JsonObject> createConverter(ExportOptions options, Permission permission) {
        return (md) -> md.writeJSON();
    }

    @Override
    public String getExtension() {
        return "json";
    }
}
