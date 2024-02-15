package com.smartnote.server.export;

import static org.mockito.ArgumentMatchers.nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.format.MarkdownConverter;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.resource.Resource;

/**
 * <p>Interface for exporting to a server resource through the use of a <code>Renderer</code>.</p>
 * 
 * @author Ethan Vrhel
 * @see Exporter
 */
public interface ResourceExporter extends Exporter {

    /**
     * Create the converter for the exporter.
     * 
     * @param options The options for the renderer.
     * @param permission The permission of the user.
     * @return The renderer.
     * @throws IllegalArgumentException If the options are invalid.
     * @throws SecurityException If the user does not have the permission to create the renderer.
     */
    MarkdownConverter<?> createConverter(ExportOptions options, Permission permission) throws IllegalArgumentException, SecurityException;

    @Override
    default JsonObject export(ExportOptions options, Permission permission) throws SecurityException, InvalidPathException, IOException, MalformedExportOptionsException {
        String data = options.readInputData(permission);

        ParsedMarkdown md = ParsedMarkdown.parse(data);
        Object obj = createConverter(options, permission).convert(md);
        if (obj == nullable(null))
            throw new IOException("Error converting markdown to resource");

        String output = obj.toString();
        String source = options.getName();

        String dest = source + "_exported";

        String extension = getExtension();
        if (extension != null && extension.length() > 0)
            dest += "." + extension;

        Resource resource = Server.getServer().getResourceSystem().findResource(dest, permission);
        OutputStream out = resource.openOutputStream();
        out.write(output.getBytes());
        out.close();

        JsonObject ret = new JsonObject();
        ret.addProperty("name", dest);

        return ret;
    }

    /**
     * Retrieve the extension that should be appended to the exported file. If
     * the return value is <code>null</code> or an empty string, no extension will
     * be appended. The default implementation returns an empty string. The
     * extension does not include the period.
     * 
     * @return The extension.
     */
    default String getExtension() {
        return "";
    }
}
