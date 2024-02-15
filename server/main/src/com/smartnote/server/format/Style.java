package com.smartnote.server.format;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.util.JSONObjectSerializable;

/**
 * Describe a style in Notion's internal format.
 * 
 * @author Ethan Vrhel
 */
public record Style(boolean bold, boolean italic, boolean strikethrough, boolean underline, boolean code, String link)
        implements JSONObjectSerializable {
    /**
     * Create a default style.
     */
    public Style() {
        this(false, false, false, false, false, null);
    }

    /**
     * Return a new style with bold enabled.
     * 
     * @return The new style.
     */
    public Style withBold() {
        return new Style(true, italic, strikethrough, underline, code, link);
    }

    /**
     * Return a new style with italic enabled.
     * 
     * @return The new style.
     */
    public Style withItalic() {
        return new Style(bold, true, strikethrough, underline, code, link);
    }

    /**
     * Return a new style with strikethrough enabled.
     * 
     * @return The new style.
     */
    public Style withStrikethrough() {
        return new Style(bold, italic, true, underline, code, link);
    }

    /**
     * Return a new style with underline enabled.
     * 
     * @return The new style.
     */
    public Style withUnderline() {
        return new Style(bold, italic, strikethrough, true, code, link);
    }

    /**
     * Return a new style with code enabled.
     * 
     * @return The new style.
     */
    public Style withCode() {
        return new Style(bold, italic, strikethrough, underline, true, link);
    }

    /**
     * Return a new style with a link.
     * 
     * @param link The link.
     * @return The new style.
     */
    public Style withLink(String link) {
        return new Style(bold, italic, strikethrough, underline, code, link);
    }

    public static Style fromJSON(JsonObject json) {
        JsonElement e;
        JsonPrimitive p;

        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        boolean code = false;
        String link = null;

        if ((e = json.get("bold")) != null && e.isJsonPrimitive() && (p = e.getAsJsonPrimitive()).isBoolean())
            bold = p.getAsBoolean();

        if ((e = json.get("italic")) != null && e.isJsonPrimitive() && (p = e.getAsJsonPrimitive()).isBoolean())
            italic = p.getAsBoolean();

        if ((e = json.get("strikethrough")) != null && e.isJsonPrimitive() && (p = e.getAsJsonPrimitive()).isBoolean())
            strikethrough = p.getAsBoolean();

        if ((e = json.get("underline")) != null && e.isJsonPrimitive() && (p = e.getAsJsonPrimitive()).isBoolean())
            underline = p.getAsBoolean();

        if ((e = json.get("code")) != null && e.isJsonPrimitive() && (p = e.getAsJsonPrimitive()).isBoolean())
            code = p.getAsBoolean();

        if ((e = json.get("link")) != null && e.isJsonPrimitive() && (p = e.getAsJsonPrimitive()).isString())
            link = p.getAsString();

        return new Style(bold, italic, strikethrough, underline, code, link);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        if (bold)
            json.addProperty("bold", true);

        if (italic)
            json.addProperty("italic", true);

        if (strikethrough)
            json.addProperty("strikethrough", true);

        if (underline)
            json.addProperty("underline", true);

        if (code)
            json.addProperty("code", true);

        return json;
    }

    @Override
    public void loadJSON(JsonObject json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot load a Style from JSON");
    }
}