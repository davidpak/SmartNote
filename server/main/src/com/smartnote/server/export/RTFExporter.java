package com.smartnote.server.export;

import java.security.Permission;

import org.commonmark.renderer.Renderer;

import com.google.gson.JsonObject;
import com.smartnote.server.format.rtf.RTFRenderer;

/**
 * <p>Exports to RTF.</p>
 * 
 * @author Ethan Vrhel
 * @see ResourceExporter
 */
@ExporterInfo(name = "rtf")
public class RTFExporter implements ResourceExporter {
    @Override
    public Renderer createRenderer(JsonObject options, Permission permission) {
        return new RTFRenderer();
    }

    @Override
    public String getExtension() {
        return "rtf";
    }
}
