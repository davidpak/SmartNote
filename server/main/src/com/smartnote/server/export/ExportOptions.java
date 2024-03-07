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

/**
 * <p>Specifies options for exporting data.</p>
 * 
 * @author Ethan Vrhel
 */
public class ExportOptions {
    private String source;
    private Exporter exporter;
    private String output;
    private JsonObject remote;

    private String data;

    private String extended;

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

    /**
     * Set the extended error message.
     * 
     * @param extended The extended error message.
     */
    public void setExtended(String extended) {
        this.extended = extended;
    }

    public String getExtended() {
        return extended;
    }

    /**
     * Read input data from the source. If the data is already read, it will not be read again.
     * If the data was explicitly set, it will be returned.
     * 
     * @param permission The permission to use.
     * @return The input data.
     * @throws InvalidPathException If the path is invalid.
     * @throws NoSuchResourceException If the resource does not exist.
     * @throws SecurityException If the resource is not accessible.
     * @throws IOException If an I/O error occurs.
     */
    public String readInputData(Permission permission) throws InvalidPathException, NoSuchResourceException, SecurityException, IOException {
        if (data != null)
            return this.data;

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
    
    /**
     * Parses the given options.
     * 
     * @param options The options to parse.
     * @throws IllegalArgumentException If the options are invalid.
     * @throws NoSuchElementException If the options are missing required elements.
     */
    public void parse(JsonObject options) throws IllegalArgumentException, NoSuchElementException {
        source = getStringOrNull(options, "source");
        data = getStringOrNull(options, "data");
        if (source == null && data == null)
            throw new IllegalArgumentException("need source or data");

        String exporter = getStringOrNull(options, "exporter");
        if (exporter == null)
            throw new IllegalArgumentException("exporter");

        this.exporter = Exporters.getExporters().getExporter(exporter);

        output = getStringOrNull(options, "output");
        remote = getObjectOrNull(options, "remote");
    }
}
