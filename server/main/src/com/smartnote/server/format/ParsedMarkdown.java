package com.smartnote.server.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.util.JSONObjectSerializable;

public class ParsedMarkdown implements JSONObjectSerializable {
    public static ParsedMarkdown parse(String markdown) {
        Node document = Parser.builder().build().parse(markdown);

        JsonObject parsed = new JsonObject();
        JSONVisitor visitor = new JSONVisitor(parsed);
        document.accept(visitor);

        ParsedMarkdown parsedMarkdown = new ParsedMarkdown();
        parsedMarkdown.loadJSON(parsed);
        return parsedMarkdown;
    }

    private JsonObject parsed;
    private List<ParsedMarkdown> children;

    private String literal;
    private Style style;
    private String language;
    private int level;

    public ParsedMarkdown() {
        this.parsed = new JsonObject();
        this.children = new ArrayList<>();
        this.literal = null;
        this.style = new Style();
        this.language = null;
        this.level = 0;
    }

    public String getType() {
        return parsed.get("type").getAsString();
    }

    public List<ParsedMarkdown> getChildren() {
        return children;
    }

    public JsonObject getParsedJson() {
        return parsed;
    }

    public String getLiteral() {
        return literal;
    }

    public Style getStyle() {
        return style;
    }

    public String getLanguage() {
        return language;
    }

    public int getLevel() {
        return level;
    }

    public void accept(MarkdownVisitor visitor) {
        switch (getType()) {
            case "blockQuote":
                visitor.visitBlockQuote(this);
                break;
            case "bulletList":
                visitor.visitBulletList(this);
                break;
            case "document":
                visitor.visitDocument(this);
                break;
            case "fencedCodeBlock":
                visitor.visitFencedCodeBlock(this);
                break;
            case "hardLineBreak":
                visitor.visitHardLineBreak(this);
                break;
            case "heading":
                visitor.visitHeading(this);
                break;
            case "thematicBreak":
                visitor.visitThematicBreak(this);
                break;
            case "indentedCodeBlock":
                visitor.visitIndentedCodeBlock(this);
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
            case "text":
                visitor.visitText(this);
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

    @Override
    public void loadJSON(JsonObject json) {
        this.parsed = json;

        JsonArray childrenJson = parsed.getAsJsonArray("children");
        this.children = new ArrayList<>();
        if (childrenJson != null) {
            for (int i = 0; i < childrenJson.size(); i++) {
                ParsedMarkdown md = new ParsedMarkdown();
                md.loadJSON(childrenJson.get(i).getAsJsonObject());
                this.children.add(md);
            }
        }
        this.children = Collections.unmodifiableList(this.children);

        JsonPrimitive literalPrimitive = parsed.getAsJsonPrimitive("literal");
        if (literalPrimitive != null)
            this.literal = literalPrimitive.getAsString();

        JsonObject styleObject = parsed.getAsJsonObject("style");
        if (styleObject != null)
            this.style = Style.fromJSON(styleObject);
        else
            this.style = new Style();

        JsonPrimitive languagePrimitive = parsed.getAsJsonPrimitive("language");
        if (languagePrimitive != null)
            this.language = languagePrimitive.getAsString();

        JsonPrimitive levelPrimitive = parsed.getAsJsonPrimitive("level");
        if (levelPrimitive != null)
            this.level = levelPrimitive.getAsInt();
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        for (var entry : parsed.entrySet())
            json.add(entry.getKey(), entry.getValue().deepCopy());
        return json;
    }
}
