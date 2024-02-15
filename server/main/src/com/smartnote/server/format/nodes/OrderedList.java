package com.smartnote.server.format.nodes;

import java.util.List;

import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownVisitor;

public class OrderedList extends MarkdownNode {
    private int startNumber;

    public OrderedList(int startNumber, List<MarkdownNode> children) {
        super(children);
        this.startNumber = startNumber;
    }

    public int getStartNumber() {
        return startNumber;
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);
        json.addProperty("startNumber", startNumber);
        return json;
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "orderedList";
    }
}
