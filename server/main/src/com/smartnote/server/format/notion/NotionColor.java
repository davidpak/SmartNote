package com.smartnote.server.format.notion;

import com.google.gson.JsonPrimitive;
import com.smartnote.server.util.JSONSerializable;

/**
 * <p>
 * Represents a color in Notion rich text.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichText
 */
record NotionColor(String color) implements JSONSerializable<JsonPrimitive> {
    public static final NotionColor DEFAULT = new NotionColor("default");
    public static final NotionColor BLUE = new NotionColor("blue");
    public static final NotionColor BLUE_BACKGROUND = new NotionColor("blue_background");
    public static final NotionColor BROWN = new NotionColor("brown");
    public static final NotionColor BROWN_BACKGROUND = new NotionColor("brown_background");
    public static final NotionColor GRAY = new NotionColor("gray");
    public static final NotionColor GRAY_BACKGROUND = new NotionColor("gray_background");
    public static final NotionColor GREEN = new NotionColor("green");
    public static final NotionColor GREEN_BACKGROUND = new NotionColor("green_background");
    public static final NotionColor ORANGE = new NotionColor("orange");
    public static final NotionColor ORANGE_BACKGROUND = new NotionColor("orange_background");
    public static final NotionColor PINK = new NotionColor("pink");
    public static final NotionColor PINK_BACKGROUND = new NotionColor("pink_background");
    public static final NotionColor PURPLE = new NotionColor("purple");
    public static final NotionColor PURPLE_BACKGROUND = new NotionColor("purple_background");
    public static final NotionColor RED = new NotionColor("red");
    public static final NotionColor RED_BACKGROUND = new NotionColor("red_background");
    public static final NotionColor YELLOW = new NotionColor("yellow");
    public static final NotionColor YELLOW_BACKGROUND = new NotionColor("yellow_background");

    /**
     * <p>
     * Create a new NotionColor with the default color.
     * </p>
     */
    public NotionColor() {
        this(DEFAULT.color);
    }

    /**
     * <p>
     * Checks if the color is the default color.
     * </p>
     * 
     * @return <code>true</code> if the color is the default color
     *         and <code>false</code> otherwise
     */
    public boolean isDefault() {
        return color.equals(DEFAULT.color);
    }

    @Override
    public JsonPrimitive writeJSON() {
        return new JsonPrimitive(color);
    }

    @Override
    public void loadJSON(JsonPrimitive json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NotionColor c) {
            return c.color.equals(color);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }
}
