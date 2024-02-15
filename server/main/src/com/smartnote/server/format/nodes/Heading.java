package com.smartnote.server.format.nodes;

import java.util.List;

import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownVisitor;

public class Heading extends MarkdownNode {
    private int level;

    public Heading(int level, List<MarkdownNode> children) {
        super(children);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);
        json.addProperty("level", level);
        return json;
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "heading";
    }
}
