package com.smartnote.server.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ParsedMarkdown {
    public static ParsedMarkdown parse(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        JsonObject parsed = new JsonObject();
        JSONVisitor visitor = new JSONVisitor(parsed);
        document.accept(visitor);

        return new ParsedMarkdown(parsed);
    }

    private JsonObject parsed;
    private List<ParsedMarkdown> children;
    private Style style;

    private ParsedMarkdown(JsonObject parsed) {
        this.parsed = parsed;

        JsonArray childrenJson = parsed.getAsJsonArray("children");
        this.children = new ArrayList<>();
        if (childrenJson != null) {
            for (int i = 0; i < childrenJson.size(); i++)
                this.children.add(new ParsedMarkdown(childrenJson.get(i).getAsJsonObject()));
        }
        this.children = Collections.unmodifiableList(this.children);

        JsonObject styleObject = parsed.getAsJsonObject("style");
        if (styleObject != null)
            this.style = Style.fromJSON(styleObject);
    }

    public String getType() {
        return parsed.get("type").getAsString();
    }

    public List<ParsedMarkdown> getChildren() {
        return children;
    }

    public void accept(MarkdownVisitor visitor) {
        switch (getType()) {
            case "blockQuote":
                visitor.visitBlockQuote(this);
                break;
            case "bulletList":
                visitor.visitBulletList(this);
                break;
            case "code":
                visitor.visitCode(this
                break;
            case "document":
                visitor.visitDocument(this);
                break;
            case "fencedCodeBlock":
                visitor.visitFencedCodeBlock(this, Style.fromJSON(parsed.get("style").getAsJsonObject()),
                        parsed.get("language").getAsString());
                break;
            case "hardLineBreak":
                visitor.visitHardLineBreak(this);
                break;
            case "heading":
                visitor.visitHeading(this, parsed.get("level").getAsInt());
                break;
            case "thematicBreak":
                visitor.visitThematicBreak(this);
                break;
            case "indentedCodeBlock":
                visitor.visitIndentedCodeBlock(this, Style.fromJSON(parsed.get("style").getAsJsonObject()));
                break;
            case "listItem":
                visitor.visitListItem(this);
                break;
            case "orderedList":
                visitor.visitOrderedList(this);
                break;
            case "paragraph":
                visitor.visitParagraph(this);
                break;
            case "softLineBreak":
                visitor.visitSoftLineBreak(this);
                break;
            default:
                throw new RuntimeException("Unknown type: " + getType());
        }
    }

    public String prettyPrint() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(parsed);
    }

    @Override
    public String toString() {
        return new Gson().toJson(parsed);
    }
}
