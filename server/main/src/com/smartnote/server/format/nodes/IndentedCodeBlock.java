package com.smartnote.server.format.nodes;

import com.google.gson.JsonObject;
import com.smartnote.server.format.MarkdownVisitor;

public class IndentedCodeBlock extends MarkdownNode {
    private String literal;

    public IndentedCodeBlock(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);
        json.addProperty("literal", literal);
        return json;
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "indentedCodeBlock";
    }

}
