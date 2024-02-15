package com.smartnote.server.export;

import static com.smartnote.server.util.JSONUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;
import java.util.NoSuchElementException;

import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;

public class ExportOptions {
    private String source;
    private Exporter exporter;
    private String output;
    private JsonObject remote;

    private String data;

    public String getSource() {
        return source;
    }

    public Exporter getExporter() {
        return exporter;
    }

    public String getOutput() {
        return output;
    }

    public JsonObject getRemote() {
        return remote;
    }

    public String getData() {
        return data;
    }

    public String readInputData(Permission permission) throws InvalidPathException, NoSuchResourceException, SecurityException, IOException {
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(source, permission);

        InputStream in = null;
        try {
            in = resource.openInputStream();
            this.data = new String(in.readAllBytes());
        } finally {
            if (in != null)
                in.close();
        }

        return this.data;
    }
    
    public void parse(JsonObject options) throws IllegalArgumentException, NoSuchElementException {
        source = getStringOrNull(options, "source");
        if (source == null)
            throw new IllegalArgumentException("source");

        String exporter = getStringOrNull(options, "exporter");
        if (exporter == null)
            throw new IllegalArgumentException("exporter");

        this.exporter = Exporters.getExporters().getExporter(exporter);

        output = getStringOrNull(options, "output");
        remote = getObjectOrNull(options, "remote");
    }
}
