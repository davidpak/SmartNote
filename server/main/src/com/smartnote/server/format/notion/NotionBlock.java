package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.smartnote.server.util.JSONObjectSerializable;
import com.smartnote.server.util.JSONUtil;

/**
 * <p>
 * Base class for a
 * <a href="https://developers.notion.com/reference/block">Notion block</a>.
 * Blocks can be nested and contain rich text, lists, code, and other blocks.
 * </p>
 * 
 * <p>
 * Blocks can be serialized to JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see JSONObjectSerializable
 */
abstract class NotionBlock implements JSONObjectSerializable {
    private final List<NotionBlock> children = new ArrayList<>();
    private NotionBlock parent;

    /**
     * <p>
     * Add a child block to this block. If the block is already a
     * child of another block, it is first removed from that block.
     * </p>
     * 
     * @param child The child to add.
     */
    public void add(NotionBlock child) {
        if (child.parent != null)
            child.parent.children.remove(child);

        children.add(child);
        child.parent = this;
    }

    /**
     * <p>
     * Get the parent block of this block, i.e. the block that contains
     * this block as a child.
     * </p>
     * 
     * @return The parent block, or <code>null</code> if this block has
     *         no parent.
     */
    public NotionBlock getParent() {
        return parent;
    }

    /**
     * <p>
     * Get the type of the block. This is a string that represents the
     * type of the block, e.g. <code>"paragraph"</code>,
     * <code>"heading_1"</code>, <code>"bulleted_list_item"</code>,
     * etc. This should be the string that would be stored in the
     * <code>"object"</code> field of the JSON object representing the block.
     * See the Notion API documentation for more information.
     * </p>
     * 
     * @return The type of the block.
     */
    public abstract String getType();

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("type", getType());

        if (children.size() > 0) {
            JsonArray childrenArray = new JsonArray();
            children.stream().map(NotionBlock::writeJSON).forEach(childrenArray::add);
            json.add("children", childrenArray);
        }
        return json;
    }

    @Override
    public void loadJSON(JsonObject object) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

/**
 * <p>
 * Represents a block of rich text in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionBlock
 */
abstract class NotionRichText extends NotionBlock {
    // annotations
    boolean bold;
    boolean italic;
    boolean strikethrough;
    boolean underline;
    boolean code;
    NotionColor color = NotionColor.DEFAULT;

    // other fields
    String plainText;
    String href;

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        JsonObject annotations = createAnnotationsObject();
        if (annotations != null)
            json.add("annotations", annotations);

        if (plainText != null)
            json.addProperty("plain_text", plainText);

        if (href != null)
            json.addProperty("href", href);

        return json;
    }

    /**
     * <p>
     * Create the <code>"annotations"</code> object for this rich text.
     * </p>
     * 
     * @return The annotations object, or <code>null</code> if there
     *         are no annotations that vary from the default.
     */
    private JsonObject createAnnotationsObject() {
        JsonObject annotationsObject = new JsonObject();
        if (bold)
            annotationsObject.addProperty("bold", bold);
        if (italic)
            annotationsObject.addProperty("italic", italic);
        if (strikethrough)
            annotationsObject.addProperty("strikethrough", strikethrough);
        if (underline)
            annotationsObject.addProperty("underline", underline);
        if (code)
            annotationsObject.addProperty("code", code);
        if (!color.isDefault())
            annotationsObject.addProperty("color", color.color());
        return annotationsObject.size() > 0 ? annotationsObject : null;
    }
}

/**
 * <p>
 * Represents a block of text in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichText
 */
class NotionText extends NotionRichText {
    // text
    String content;
    String link;

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        JsonObject textObject = new JsonObject();
        textObject.addProperty("content", content);
        textObject.addProperty("link", link);
        json.add("text", textObject);

        return json;
    }
}

/**
 * <p>
 * Represents a block that contains an array of rich text
 * blocks. These blocks can be nested and contain rich text,
 * lists, code, and other blocks.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionBlock
 */
abstract class NotionRichTextArray extends NotionBlock {
    private final List<NotionRichText> richText = new ArrayList<>();

    NotionColor color = NotionColor.DEFAULT;

    /**
     * <p>
     * Add a rich text block to this block.
     * </p>
     * 
     * @param block The rich text block to add.
     */
    public void addRichText(NotionRichText block) {
        richText.add(block);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        JsonObject internalObject = new JsonObject();
        JsonArray array = richText.stream()
                .map(NotionRichText::writeJSON)
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        internalObject.add("rich_text", array);

        if (!color.isDefault())
            internalObject.add("color", color.writeJSON());
        json.add(getType(), internalObject);

        return json;
    }
}

/**
 * <p>
 * Represents a block of text in Notion that is part of a
 * paragraph.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichTextArray
 */
class NotionParagraph extends NotionRichTextArray {
    @Override
    public String getType() {
        return "paragraph";
    }
}

/**
 * <p>
 * Represents a heading block in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichTextArray
 */
class NotionHeading extends NotionRichTextArray {
    int level;
    boolean isToggleable;

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        if (isToggleable)
            json.addProperty("is_toggleable", isToggleable);

        return json;
    }

    @Override
    public String getType() {
        return "heading_" + level;
    }
}

/**
 * <p>
 * Represents a bulleted list block in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichTextArray
 */
class NotionBulletedList extends NotionRichTextArray {
    @Override
    public String getType() {
        return "bulleted_list_item";
    }
}

/**
 * <p>
 * Represents a numbered list block in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichTextArray
 */
class NotionNumberedList extends NotionRichTextArray {
    @Override
    public String getType() {
        return "numbered_list_item";
    }
}

/**
 * <p>
 * Represents a block quote block in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionBlock
 */
class NotionCode extends NotionBlock {
    String language;

    private final List<NotionRichText> caption = new ArrayList<>();
    private final List<NotionRichText> code = new ArrayList<>();

    /**
     * <p>
     * Add a rich text block to the caption of this code block.
     * </p>
     * 
     * @param richText The rich text block to add.
     */
    public void addCaption(NotionRichText richText) {
        caption.add(richText);
    }

    /**
     * <p>
     * Add a rich text block to the code of this code block.
     * </p>
     * 
     * @param richText The rich text block to add.
     */
    public void addCode(NotionRichText richText) {
        code.add(richText);
    }

    @Override
    public JsonObject writeJSON(JsonObject object) {
        object.addProperty("type", "code");

        JsonObject codeObject = new JsonObject();
        codeObject.add("caption", JSONUtil.toArray(caption));
        codeObject.add("code", JSONUtil.toArray(code));
        codeObject.addProperty("language", language);
        object.add("code", codeObject);

        return super.writeJSON(object);
    }

    @Override
    public String getType() {
        return "code";
    }
}
