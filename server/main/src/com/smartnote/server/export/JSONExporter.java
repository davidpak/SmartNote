package com.smartnote.server.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.format.json.JSONRenderer;
import com.smartnote.server.resource.Resource;

@ExporterInfo(name = "json")
public class JSONExporter implements Exporter {
    
    @Override
    public JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException, InvalidPathException, IOException, ExportException {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(data);
        String jsonString = new JSONRenderer().render(document);

        String source = options.get("name").getAsString();
        String dest = source + "_exported.json";

        Resource resource = Server.getServer().getResourceSystem().findResource(dest, permission);
        OutputStream out = resource.openOutputStream();
        out.write(jsonString.getBytes());
        out.close();

        JsonObject ret = new JsonObject();
        ret.addProperty("name", dest);

        return ret;
    }
}
