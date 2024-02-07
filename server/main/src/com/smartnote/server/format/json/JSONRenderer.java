package com.smartnote.server.format.json;

import org.commonmark.node.Node;
import org.commonmark.renderer.Renderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * <p>
 * Converts Markdown to JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see JSONVisitor
 */
public class JSONRenderer implements Renderer {
    private boolean prettyPrint;

    public JSONRenderer setPrettyPrinting() {
        prettyPrint = true;
        return this;
    }

    @Override
    public void render(Node node, Appendable output) {
        JsonObject json = new JsonObject();

        JSONVisitor visitor = new JSONVisitor(json);
        node.accept(visitor);

        GsonBuilder builder = new GsonBuilder();
        if (prettyPrint)
            builder.setPrettyPrinting();

        Gson gson = builder.create();
        gson.toJson(json, output);
    }

    @Override
    public String render(Node node) {
        StringBuilder sb = new StringBuilder();
        render(node, sb);
        return sb.toString();
    }
}
