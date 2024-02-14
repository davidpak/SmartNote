package com.smartnote.server.format.notion;

import org.commonmark.node.Node;
import org.commonmark.renderer.Renderer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartnote.server.format.Converter;

/**
 * <p>
 * Converts markdown to Notion's internal format.
 * </p>
 * 
 * <p>
 * Notion's internal format is a JSON object that
 * represents the content of a Notion page. This class is
 * responsible for converting the markdown AST to this
 * JSON object. Consult the Notion API documentation for
 * more information
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionVisitor
 */
public class NotionConverter implements Converter<JsonObject> {

    @Override
    public JsonObject convert(JsonObject document) {
        
    }
}
