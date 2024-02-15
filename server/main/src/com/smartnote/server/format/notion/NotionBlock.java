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

    private List<RichText> richText;
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

    public NotionBlock addRichText(RichText richText) {
        this.richText.add(richText);
        return this;
    }

    /**
     * Add rich text to the block.
     * 
     * @param literal The literal text.
     * @param style   The style of the text. If <code>null</code>,
     *                the text will be plain.
     * @return <code>this</code>
     */
    public NotionBlock addRichText(String literal, Style style) {
        return addRichText(new RichText(literal, style));
    }

    /**
     * Add rich text to the block.
     * 
     * @param literal The literal text.
     * @return <code>this</code>
     */
    public NotionBlock addRichText(String literal) {
        return addRichText(literal, null);
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

    public NotionBlock findFirstOf(String type) {
        for (NotionBlock block : children) {
            if (block.getType().equals(type))
                return block;
        }

        for (NotionBlock block : children) {
            NotionBlock found = block.findFirstOf(type);
            if (found != null)
                return found;
        }

        return null;
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
    public List<RichText> getRichText() {
        return Collections.unmodifiableList(richText);
    }

    /**
     * Infers the plain text from the rich text.
     * 
     * @return The plain text.
     */
    public String getPlainText() {
        if (richText.size() == 0)
            return null;

        JsonObject textObject = richText.get(0).writeJSON();
        JsonObject textDataObject = textObject.getAsJsonObject("text");
        return textDataObject.get("content").getAsString();
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
            for (RichText text : richText)
                richTextArray.add(text.writeJSON());
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