package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

import com.google.gson.*;
import com.smartnote.server.util.Entry;
import com.smartnote.server.util.JSONObjectSerializable;
import com.smartnote.server.util.JSONUtil;

/**
 * <p>
 * Represents a Notion object. Notion objects can be serialized to JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see JSONObjectSerializable
 */
abstract class NotionObject implements JSONObjectSerializable {
    public Entry<String, NotionObject> entry() {
        return new Entry<String, NotionObject>(getName(), this);
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

    /**
     * <p>
     * Get the name of the block. This will be used as the name within
     * the Notion page if the block is the top-level block of a page.
     * This defaults to the type of the block, but can be overridden
     * by subclasses.
     * </p>
     * 
     * @return The name.
     */
    public String getName() {
        return getType();
    }

    @Override
    public void loadJSON(JsonObject object) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

/**
 * <p>
 * Represents a Notion object that contains other Notion objects.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionObject
 */
abstract class NotionObjectCollection<T extends NotionObject> extends NotionObject implements Collection<T> {
    private final List<T> objects = new ArrayList<>();

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return objects.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        // to prevent removal of children
        return new Iterator<>() {
            private final Iterator<T> iterator = objects.iterator();
            
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return objects.toArray();
    }

    @Override
    public <Q> Q[] toArray(Q[] a) {
        return objects.toArray(a);
    }

    @Override
    public boolean add(T object) {
        objects.add(object);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return objects.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return objects.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) throws UnsupportedOperationException {
        return objects.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return objects.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return objects.retainAll(c);
    }

    @Override
    public void clear() {
        objects.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NotionObjectCollection<?> t)
            return t.objects.equals(objects);
        return false;
    }

    @Override
    public int hashCode() {
        return objects.hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        return objects.spliterator();
    }

    @Override
    public String toString() {
        return getType() + " [size=" + objects.size() + "]";
    }
}

/**
 * <p>
 * Represents the <code>"properties"</code> object of a Notion page.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionObjectCollection
 * @see NotionPage
 */
class NotionPageProperties extends NotionObjectCollection<NotionBlock> {
    @Override
    public String getType() {
        return "properties";
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        return JSONUtil.toObject(stream().map(NotionObject::entry));
    }
}

/**
 * <p>
 * Represents a Notion page.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionObjectCollection
 * @see NotionPageProperties
 */
class NotionPage extends NotionObjectCollection<NotionObject> {
    private NotionPageProperties properties = new NotionPageProperties();

    public NotionPageProperties getProperties() {
        return properties;
    }

    @Override
    public String getType() {
        return "page";
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("object", getType());
        json.add("properties", properties.writeJSON());
        return json;
    }    
}

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
abstract class NotionBlock extends NotionObjectCollection<NotionBlock> {
    private NotionBlock parent;

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

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("type", getType());

        if (size() > 0) 
            json.add("children", JSONUtil.toArray(this));

        return json;
    }

    @Override
    public boolean add(NotionBlock block) {
        if (block.parent != null)
            block.parent.remove(block);
        block.parent = this;
        return super.add(block);
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
class NotionCode extends NotionRichTextArray {
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

/**
 * <p>
 * Represents an inline code block in Notion.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRichTextArray
 */
class NotionQuote extends NotionRichTextArray {
    @Override
    public String getType() {
        return "quote";
    }
}
