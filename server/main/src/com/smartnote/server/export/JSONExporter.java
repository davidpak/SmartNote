package com.smartnote.server.export;

import java.security.Permission;

import org.commonmark.renderer.Renderer;

import com.google.gson.JsonObject;
import com.smartnote.server.format.json.JSONRenderer;

/**
 * <p>Exports to JSON.</p>
 * 
 * @author Ethan Vrhel
 * @see ResourceExporter
 */
@ExporterInfo(name = "json")
public class JSONExporter implements ResourceExporter {
    @Override
    public Renderer createRenderer(JsonObject options, Permission permission) {
        return new JSONRenderer();
    }

    @Override
    public String getExtension() {
        return "json";
    }
}
