package com.smartnote.server.format.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownVisitor;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.util.JSONObjectSerializable;

/**
 * Represents a node in the Markdown AST. This exists over commonmark's
 * implementation as it provides an easier way for serialization. Especially
 * with respect to text styling. Nodes are immutable.
 * 
 * @author Ethan Vrhel
 * @see ParsedMarkdown
 */
public abstract class MarkdownNode implements JSONObjectSerializable {
    private MarkdownNode[] children;
    private MarkdownNode parent;

    /**
     * Constructs a new MarkdownNode.
     * 
     * @param children The children of the node. Can be <code>null</code>.
     */
    public MarkdownNode(List<MarkdownNode> children) {
        if (children != null) {
            this.children = new MarkdownNode[children.size()];
            for (int i = 0; i < children.size(); i++) {
                this.children[i] = children.get(i);
                this.children[i].parent = this;
            }
        } else {
            this.children = new MarkdownNode[0];
        }
    }

    /**
     * Constructs a new MarkdownNode with no children.
     */
    public MarkdownNode() {
        this(null);
    }

    /**
     * Returns the parent of the node.
     * 
     * @return The parent of the node.
     */
    public MarkdownNode getParent() {
        return parent;
    }

    /**
     * Gets the children of the node.
     * 
     * @return The children of the node.
     */
    public MarkdownNode[] getChildren() {
        MarkdownNode[] copy = new MarkdownNode[children.length];
        System.arraycopy(children, 0, copy, 0, children.length);
        return copy;
    }

    /**
     * Accepts a visitor, calling the appropriate visit method.
     * 
     * @param visitor The visitor to accept.
     */
    public abstract void accept(MarkdownVisitor visitor);

    /**
     * Gets the type of the node.
     * 
     * @return The type of the node.
     */
    public abstract String getType();

    @Override
    public String toString() {
        return new Gson().toJson(writeJSON());
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("type", getType());

        if (children.length > 0) {
            JsonArray childrenArray = new JsonArray();
            for (MarkdownNode md : children)
                childrenArray.add(md.writeJSON());
            json.add("children", childrenArray);
        }

        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        throw new UnsupportedOperationException("Use ParsedMarkdown to load from JSON");
    }

    /**
     * Visits the children of the node.
     * 
     * @param visitor The visitor to accept.
     */
    public void visitChildren(MarkdownVisitor visitor) {
        for (MarkdownNode md : children)
            md.accept(visitor);
    }
}
