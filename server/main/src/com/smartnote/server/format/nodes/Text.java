package com.smartnote.server.format.nodes;

import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownVisitor;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.Style;

public class Text extends MarkdownNode {
    private String literal;
    private Style style;

    public Text(String literal, Style style) {
        this.literal = literal;
        this.style = style;
    }

    public String getLiteral() {
        return literal;
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        json.addProperty("literal", literal);

        JsonObject styleObject = ParsedMarkdown.styleToJson(style);
        if (styleObject.size() > 0)
            json.add("style", styleObject);

        return json;
    }
}
