package com.smartnote.server.format.notion;

import static com.smartnote.server.util.FunctionalUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Supplier;

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
 * @see NotionObjectCollection
 * @see NotionBlock
 */
abstract class NotionObject implements JSONObjectSerializable {

    /**
     * Create an <code>Entry</code> object that contains the name of the
     * object and the object itself.
     * 
     * @return The entry.
     */
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
 * @see NotionBlock
 * @see NotionPage
 * @see JSONObjectSerializable
 */
abstract class NotionBlock extends NotionObjectCollection<NotionBlock> {
    private NotionBlock parent;
    private NotionPage page;

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
     * Get the Notion page that this block is a part of.
     * </p>
     * 
     * @return The Notion page.
     */
    public NotionPage getPage() {
        return page;
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
 * Stores data that goes into the <code>"annotations"</code> field of a
 * rich text block. This object is immutable.
 * </p>
 * 
 * @see RichTextData
 */
record AnnotationData(boolean bold, boolean italic, boolean strikethrough, boolean underline, boolean code,
        NotionColor color) implements JSONObjectSerializable {

    /**
     * Create a new <code>AnnotationData</code> object with all fields set to
     * <code>false</code> and the color set to the default color.
     */
    public AnnotationData() {
        this(false, false, false, false, false, NotionColor.DEFAULT);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        if (bold)
            json.addProperty("bold", bold);
        if (italic)
            json.addProperty("italic", italic);
        if (strikethrough)
            json.addProperty("strikethrough", strikethrough);
        if (underline)
            json.addProperty("underline", underline);
        if (code)
            json.addProperty("code", code);
        if (!color.isDefault())
            json.addProperty("color", color.color());
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public AnnotationData setBold() {
        return new AnnotationData(true, italic, strikethrough, underline, code, color);
    }

    public AnnotationData setItalic() {
        return new AnnotationData(bold, true, strikethrough, underline, code, color);
    }

    public AnnotationData setStrikethrough() {
        return new AnnotationData(bold, italic, true, underline, code, color);
    }

    public AnnotationData setUnderline() {
        return new AnnotationData(bold, italic, strikethrough, true, code, color);
    }

    public AnnotationData setCode() {
        return new AnnotationData(bold, italic, strikethrough, underline, true, color);
    }

    public AnnotationData setColor(NotionColor color) {
        return new AnnotationData(bold, italic, strikethrough, underline, code, color);
    }

    @Override
    public String toString() {
        return "AnnotationData [bold=" + bold + ", italic=" + italic + ", strikethrough=" + strikethrough
                + ", underline=" + underline + ", code=" + code + ", color=" + color + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnnotationData a) {
            return a.bold == bold && a.italic == italic && a.strikethrough == strikethrough && a.underline == underline
                    && a.code == code && a.color.equals(color);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(bold) + 2 * Boolean.hashCode(italic) + 4 * Boolean.hashCode(strikethrough)
                + 8 * Boolean.hashCode(underline) + 16 * Boolean.hashCode(code) + 32 * color.hashCode();
    }
}

/**
 * <p>
 * Stores information about rich text. This object is immutable.
 * </p>
 * 
 * <p>
 * Differing styles of rich text can be generated by using one of the
 * <code>set*()</code> methods or by using the static methods that
 * compose a function that describes how to create a new
 * <code>RichTextData</code> object.
 * </p>
 * 
 * @see NotionRichText
 * @see AnnotationData
 */
record RichTextData(AnnotationData annotations, String plainText, String href) implements JSONObjectSerializable {

    public RichTextData() {
        this(new AnnotationData(), null, null);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        JsonObject obj = annotations.writeJSON();
        if (obj.size() > 0)
            json.add("annotations", obj);

        if (plainText != null)
            json.addProperty("plain_text", plainText);

        if (href != null)
            json.addProperty("href", href);

        return json;
    }

    @Override
    public void loadJSON(JsonObject json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public RichTextData setBold() {
        return new RichTextData(annotations.setBold(), plainText, href);
    }

    public RichTextData setItalic() {
        return new RichTextData(annotations.setItalic(), plainText, href);
    }

    public RichTextData setStrikethrough() {
        return new RichTextData(annotations.setStrikethrough(), plainText, href);
    }

    public RichTextData setUnderline() {
        return new RichTextData(annotations.setUnderline(), plainText, href);
    }

    public RichTextData setCode() {
        return new RichTextData(annotations.setCode(), plainText, href);
    }

    public RichTextData setColor(NotionColor color) {
        return new RichTextData(annotations.setColor(color), plainText, href);
    }

    public RichTextData setPlainText(String plainText) {
        return new RichTextData(annotations, plainText, href);
    }

    public RichTextData setHref(String href) {
        return new RichTextData(annotations, plainText, href);
    }

    public static Supplier<RichTextData> setBold(Supplier<RichTextData> supplier) {
        return thunk(supplier, RichTextData::setBold);
    }

    public static Supplier<RichTextData> setItalic(Supplier<RichTextData> supplier) {
        return thunk(supplier, RichTextData::setItalic);
    }

    public static Supplier<RichTextData> setStrikethrough(Supplier<RichTextData> supplier) {
        return thunk(supplier, RichTextData::setStrikethrough);
    }

    public static Supplier<RichTextData> setUnderline(Supplier<RichTextData> supplier) {
        return thunk(supplier, RichTextData::setUnderline);
    }

    public static Supplier<RichTextData> setCode(Supplier<RichTextData> supplier) {
        return thunk(supplier, RichTextData::setCode);
    }

    public static Function<NotionColor, Supplier<RichTextData>> setColor(Supplier<RichTextData> supplier) {
        return defer(supplier, RichTextData::setColor);
    }

    public static Function<String, Supplier<RichTextData>> setPlainText(Supplier<RichTextData> supplier) {
        return defer(supplier, RichTextData::setPlainText);
    }

    public static Function<String, Supplier<RichTextData>> setHref(Supplier<RichTextData> supplier) {
        return defer(supplier, RichTextData::setHref);
    }

    @Override
    public String toString() {
        return "RichTextData [annotations=" + annotations + ", plainText=" + plainText + ", href=" + href + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RichTextData r) {
            return r.annotations.equals(annotations) && r.plainText.equals(plainText) && r.href.equals(href);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return annotations.hashCode() + 2 * plainText.hashCode() + 4 * href.hashCode();
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
    RichTextData richText;

    public NotionRichText() {
        this(new RichTextData());
    }

    public NotionRichText(RichTextData richText) {
        this.richText = richText;
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);
        return richText.writeJSON(json);
    }
}

record TextData(String content, String link) implements JSONObjectSerializable {
    public TextData() {
        this(null, null);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        if (content != null)
            json.addProperty("content", content);
        if (link != null)
            json.addProperty("link", link);
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public TextData content(String content) {
        return new TextData(content, link);
    }

    public TextData link(String link) {
        return new TextData(content, link);
    }

    @Override
    public String toString() {
        return "TextData [content=" + content + ", link=" + link + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TextData t) {
            return t.content.equals(content) && t.link.equals(link);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return content.hashCode() + 2 * link.hashCode();
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
    public static Supplier<NotionText> supplier(String content, RichTextData richText) {
        return () -> new NotionText(content, richText);
    }

    TextData text;

    public NotionText() {
        this(null, new RichTextData());
    }

    public NotionText(String content, RichTextData richText) {
        super(richText);
        text = text.content(content);
        this.richText = richText;
    }

    public NotionText(String content) {
        this();
        text = text.content(content);
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        super.writeJSON(json);

        JsonObject textObject = text.writeJSON();
        if (textObject.size() > 0)
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
