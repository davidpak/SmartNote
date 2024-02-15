package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.format.Style;
import com.smartnote.server.util.JSONObjectSerializable;

/**
 * <p>
 * Represents a block in Notion's internal format.
 * </p>
 * 
 * @author Ethan Vrhel
 */
public class NotionBlock implements JSONObjectSerializable {
    private String type;

    private List<JsonObject> richText;
    private List<NotionBlock> children;

    private JsonObject more;

    /**
     * Create a new block.
     * 
     * @param type The type of the block. If <code>null</code>,
     *             the block is the root block.
     */
    public NotionBlock(String type) {
        this.type = type;
        this.richText = new ArrayList<>();
        this.children = new ArrayList<>();
        this.more = new JsonObject();
    }

    /**
     * Add rich text to the block.
     * 
     * @param literal The literal text.
     * @param style   The style of the text.
     */
    public void addRichText(String literal, Style style) {
        JsonObject textObject = new JsonObject();
        textObject.addProperty("type", "text");

        JsonObject textDataObject = new JsonObject();
        textDataObject.addProperty("content", literal);

        if (style.link() != null) {
            JsonObject linkObject = new JsonObject();
            linkObject.addProperty("url", style.link());
            textDataObject.add("link", linkObject);
        }

        textObject.add("text", textDataObject);

        JsonObject annotations = style.writeJSON();
        if (annotations.size() > 0)
            textObject.add("annotations", annotations);

        richText.add(textObject);
    }

    public List<NotionBlock> findInChildren(String type) {
        List<NotionBlock> blocks = new ArrayList<>();
        for (NotionBlock block : children) {
            if (block.getType().equals(type))
                blocks.add(block);
            blocks.addAll(block.findInChildren(type));
        }
        return blocks;
    }

    /**
     * Returns the type of this block.
     * 
     * @return The type of this block.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the children of this block.
     * 
     * @return The children of this block.
     */
    public List<NotionBlock> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns the rich text of this block.
     * 
     * @return The rich text of this block.
     */
    public List<JsonObject> getRichText() {
        return Collections.unmodifiableList(richText);
    }

    /**
     * Add a child block to the block.
     * 
     * @param block The child block.
     */
    public void addChild(NotionBlock block) {
        children.add(block);
    }

    /**
     * Add an additional property to the block.
     * 
     * @param key   The key.
     * @param value The value.
     */
    public void addProperty(String key, String value) {
        more.addProperty(key, value);
    }

    @Override
    public void loadJSON(JsonObject json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot load JSON into NotionBlock");
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        JsonObject objectData = createObjectData();
        if (type == null) // represents the root block
            return objectData;

        // add object and type
        json.addProperty("object", "block");
        json.addProperty("type", type);

        // only add objectData if it has data
        if (objectData.size() > 0)
            json.add(type, objectData);

        return json;
    }

    private JsonObject createObjectData() {
        JsonObject json = new JsonObject();

        // add rich text
        if (richText.size() > 0) {
            JsonArray richTextArray = new JsonArray();
            for (JsonObject richTextObject : richText)
                richTextArray.add(richTextObject);
            json.add("rich_text", richTextArray);
        }

        // add children
        if (children.size() > 0) {
            JsonArray childrenArray = new JsonArray();
            for (NotionBlock child : children)
                childrenArray.add(child.writeJSON());
            json.add("children", childrenArray);
        }

        // add additional properties
        for (String key : more.keySet())
            json.add(key, more.get(key));

        return json;
    }
}