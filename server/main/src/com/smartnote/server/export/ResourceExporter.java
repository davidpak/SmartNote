package com.smartnote.server.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;

import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.resource.Resource;

/**
 * <p>Interface for exporting to a server resource through the use of a <code>Renderer</code>.</p>
 * 
 * @author Ethan Vrhel
 * @see Exporter
 */
public interface ResourceExporter extends Exporter {

    /**
     * Create the renderer for the exporter.
     * 
     * @param options The options for the renderer.
     * @param permission The permission of the user.
     * @return The renderer.
     * @throws IllegalArgumentException If the options are invalid.
     * @throws SecurityException If the user does not have the permission to create the renderer.
     */
    Renderer createRenderer(JsonObject options, Permission permission) throws IllegalArgumentException, SecurityException;

    @Override
    default JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException, InvalidPathException, IOException {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(data);
        String output = createRenderer(options, permission).render(document);

        String source = options.get("name").getAsString();

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
