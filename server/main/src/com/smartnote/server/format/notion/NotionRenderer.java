package com.smartnote.server.format.notion;

import org.commonmark.node.Node;
import org.commonmark.renderer.Renderer;

import com.google.gson.Gson;

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
public class NotionRenderer implements Renderer {

    @Override
    public void render(Node node, Appendable output) {
        NotionPage page = new NotionPage();
        NotionVisitor visitor = new NotionVisitor(page);
        node.accept(visitor);
        new Gson().toJson(page.writeJSON(), output);
    }

    @Override
    public String render(Node node) {
        StringBuilder builder = new StringBuilder();
        render(node, builder);
        return builder.toString();
    }

}
