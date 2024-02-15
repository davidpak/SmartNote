package com.smartnote.server.format;

import static com.smartnote.server.util.JSONUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartnote.server.format.nodes.BlockQuote;
import com.smartnote.server.format.nodes.BulletList;
import com.smartnote.server.format.nodes.Document;
import com.smartnote.server.format.nodes.FencedCodeBlock;
import com.smartnote.server.format.nodes.HardLineBreak;
import com.smartnote.server.format.nodes.Heading;
import com.smartnote.server.format.nodes.IndentedCodeBlock;
import com.smartnote.server.format.nodes.ListItem;
import com.smartnote.server.format.nodes.MarkdownNode;
import com.smartnote.server.format.nodes.OrderedList;
import com.smartnote.server.format.nodes.Paragraph;
import com.smartnote.server.format.nodes.SoftLineBreak;
import com.smartnote.server.format.nodes.Text;
import com.smartnote.server.format.nodes.ThematicBreak;
import com.smartnote.server.util.JSONObjectSerializable;

/**
 * <p>
 * Handles parsing of markdown to the internal representation.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see MarkdownNode
 */
public class ParsedMarkdown implements JSONObjectSerializable {

    /**
     * Parse markdown from a string.
     * 
     * @param markdown The markdown to parse.
     * @return The parsed markdown.
     * @throws IllegalArgumentException If the markdown is invalid.
     */
    public static ParsedMarkdown parse(String markdown) throws IllegalArgumentException {
        Node document = Parser.builder().build().parse(markdown);

        JsonObject parsed = new JsonObject();
        JSONVisitor visitor = new JSONVisitor(parsed);
        document.accept(visitor);

        return parseFromJson(parsed);
    }

    /**
     * Parse markdown from a JSON object, laid out in the internal representation.
     * 
     * @param json The JSON object to parse.
     * @return The parsed markdown.
     * @throws IllegalArgumentException If the JSON is invalid.
     */
    public static ParsedMarkdown parseFromJson(JsonObject json) throws IllegalArgumentException {
        ParsedMarkdown parsedMarkdown = new ParsedMarkdown();
        parsedMarkdown.loadJSON(json);
        return parsedMarkdown;
    }

    private Document document;
    private JsonObject json; // cached JSON

    private ParsedMarkdown() {
        this.document = null;
    }

    /**
     * Get the document associated with this parsed markdown.
     * 
     * @return The document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Pretty print the JSON.
     * 
     * @return The pretty printed JSON.
     */
    public String prettyPrint() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(document.writeJSON());
    }

    @Override
    public String toString() {
        return document == null ? "null" : document.toString();
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        for (var entry : this.json.entrySet())
            json.add(entry.getKey(), entry.getValue().deepCopy());
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) throws IllegalArgumentException {
        MarkdownNode node = parseMarkdownNode(json);
        if (node instanceof Document document) {
            this.document = document;
            this.json = document.writeJSON(); // create a deep copy
            return;
        }

        throw new IllegalArgumentException("Root node is not a document");
    }

    private MarkdownNode parseMarkdownNode(JsonObject json) {
        List<MarkdownNode> children = getChildrenList(json);
        String type = getStringOrNull(json, "type");
        if (type == null)
            throw new IllegalArgumentException("No node type in JSON object");

        String language = getStringOrNull(json, "language");
        String literal = getStringOrNull(json, "literal");
        Style style = jsonToStyle(getObjectOrNull(json, "style"));

        switch (type) {
            case "blockQuote":
                return new BlockQuote(children);
            case "bulletList":
                return new BulletList(children);
            case "document":
                return new Document(children);
            case "fencedCodeBlock":
                return new FencedCodeBlock(language, literal, style);
            case "hardLineBreak":
                return new HardLineBreak();
            case "heading":
                return new Heading(getIntOrException(json, "level"), children);
            case "indentedCodeBlock":
                return new IndentedCodeBlock(literal);
            case "listItem":
                return new ListItem(children);
            case "orderedList":
                return new OrderedList(getIntOrDefault(json, "startNumber", 1), children);
            case "paragraph":
                return new Paragraph(children);
            case "softLineBreak":
                return new SoftLineBreak();
            case "text":
                return new Text(literal, style);
            case "thematicBreak":
                return new ThematicBreak();
            default:
                throw new IllegalArgumentException("Unknown node type: " + type);
        }
    }

    private List<MarkdownNode> getChildrenList(JsonObject json) {
        JsonArray childrenArray = getArrayOrNull(json, "children");
        if (childrenArray == null)
            return null;

        List<MarkdownNode> children = new ArrayList<>();
        for (JsonElement e : childrenArray) {
            if (e.isJsonObject()) {
                JsonObject child = e.getAsJsonObject();
                MarkdownNode node = parseMarkdownNode(child);
                children.add(node);
            }
        }

        return children;
    }

    /**
     * Convert a style to JSON for use in the internal representation.
     * 
     * @param style The style to convert.
     * @return The JSON representation of the style.
     */
    public static JsonObject styleToJson(Style style) {
        JsonObject json = new JsonObject();

        if (style == null)
            return json;

        if (style.bold())
            json.addProperty("bold", style.bold());

        if (style.italic())
            json.addProperty("italic", style.italic());

        if (style.code())
            json.addProperty("code", style.code());

        if (style.strikethrough())
            json.addProperty("strikethrough", style.strikethrough());

        if (style.underline())
            json.addProperty("underline", style.underline());

        if (style.link() != null)
            json.addProperty("link", style.link());

        return json;
    }

    /**
     * Convert a JSON object to a style.
     * 
     * @param json The JSON object to convert.
     * @return The style.
     */
    public static Style jsonToStyle(JsonObject json) {
        if (json == null)
            return new Style();

        boolean bold = getBooleanOrFalse(json, "bold");
        boolean italic = getBooleanOrFalse(json, "italic");
        boolean code = getBooleanOrFalse(json, "code");
        boolean strikethrough = getBooleanOrFalse(json, "strikethrough");
        boolean underline = getBooleanOrFalse(json, "underline");
        String link = getStringOrNull(json, "link");
        return new Style(bold, italic, strikethrough, underline, code, link);
    }
}
