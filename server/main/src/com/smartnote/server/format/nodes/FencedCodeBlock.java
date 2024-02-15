package com.smartnote.server.format.nodes;

import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownVisitor;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.Style;

public class FencedCodeBlock extends MarkdownNode {
    private String language;
    private String literal;
    private Style style;

    public FencedCodeBlock(String language, String literal, Style style) {
        this.language = language;
        this.literal = literal;
        this.style = style == null ? new Style() : style;
    }

    public String getLanguage() {
        return language;
    }

    public String getLiteral() {
        return literal;
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        json.addProperty("literal", literal);

        if (language != null)
            json.addProperty("language", language);

        JsonObject styleJson = ParsedMarkdown.styleToJson(style);
        if (styleJson.size() > 0)
            json.add("style", styleJson);

        return json;
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "fencedCodeBlock";
    }
}
