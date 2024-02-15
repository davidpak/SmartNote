package com.smartnote.server.format.notion;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.JsonObject;
import com.smartnote.server.format.Style;
import com.smartnote.server.util.JSONObjectSerializable;

/**
 * <p>
 * Simplified representation of a rich text object.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionBlock
 */
public final class RichText implements JSONObjectSerializable {
    private String literal;
    private Style style;

    /**
     * Creates a new RichText.
     * 
     * @param literal The literal.
     * @param style   The style. If <code>null</code>, the default style is used.
     */
    public RichText(String literal, Style style) {
        this.literal = literal;
        this.style = style == null ? new Style() : style;
    }

    /**
     * Creates a new RichText with the default style.
     * 
     * @param literal The literal.
     */
    public RichText(String literal) {
        this(literal, null);
    }

    /**
     * Creates a new RichText with an empty literal and the default style.
     */
    public RichText() {
        this("");
    }

    /**
     * Returns the literal.
     * 
     * @return The literal.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Returns the style.
     * 
     * @return The style.
     */
    public Style getStyle() {
        return style;
    }

    @Override
    public void loadJSON(JsonObject json) {
        this.literal = "";
        this.style = new Style();

        String type = getStringOrNull(json, "type");
        if (!type.equals("text"))
            return;

        JsonObject textDataObject = getObjectOrNull(json, "text");
        if (textDataObject == null)
            return;

        JsonObject annotations = getObjectOrNull(json, "annotations");
        if (annotations != null) {
            boolean bold = getBooleanOrFalse(annotations, "bold");
            boolean italic = getBooleanOrFalse(annotations, "italic");
            boolean strikethrough = getBooleanOrFalse(annotations, "strikethrough");
            boolean underline = getBooleanOrFalse(annotations, "underline");
            boolean code = getBooleanOrFalse(annotations, "code");

            this.style = new Style(bold, italic, strikethrough, underline, code, null);
        }

        String literal = getStringOrNull(textDataObject, "content");
        if (literal != null)
            this.literal = literal;

        String link = getStringOrNull(textDataObject, "link");
        if (link != null)
            this.style = this.style.withLink(link);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("type", "text");

        JsonObject textDataObject = new JsonObject();
        textDataObject.addProperty("content", literal);

        if (style.link() != null) {
            // JsonObject linkObject = new JsonObject();
            // linkObject.addProperty("url", style.link());
            textDataObject.addProperty("link", style.link());
        }

        json.add("text", textDataObject);

        // add annotations
        JsonObject annotations = new JsonObject();
        if (style.bold())
            annotations.addProperty("bold", true);

        if (style.italic())
            annotations.addProperty("italic", true);

        if (style.strikethrough())
            annotations.addProperty("strikethrough", true);

        if (style.underline())
            annotations.addProperty("underline", true);

        if (style.code())
            annotations.addProperty("code", true);

        if (annotations.size() > 0)
            json.add("annotations", annotations);

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o instanceof RichText rt)
            return rt.literal.equals(literal) && rt.style.equals(style);

        return false;
    }

    @Override
    public int hashCode() {
        return literal.hashCode() + style.hashCode();
    }

    @Override
    public String toString() {
        return "RichText[literal=" + literal + ", style=" + style + "]";
    }
}
