package com.smartnote.server;

import static org.junit.Assert.*;

import org.commonmark.node.Node;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.notion.NotionBlock;
import com.smartnote.server.format.notion.NotionConverter;
import com.smartnote.testing.BaseMarkdown;

/**
 * <p>Tests the NotionConverter class.</p>
 * 
 * @author Ethan Vrhel
 */
public class NotionConverterTest extends BaseMarkdown {
    private NotionConverter convert;

    private NotionBlock block;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        convert = new NotionConverter();
    }
    
    private void createBlock(String name) {
        ParsedMarkdown md = parseMarkdown(name);
        block = convert.convert(md);
        assertNotNull(block);
    }

    private void assertType(String type) {
        assertEquals(type, block.getType());
    }
    
    @Test
    public void testBasicText() {
        createBlock(BASIC_TEXT);
        
        var children = block.getChildren();
        assertEquals(1, children.size());
        
        NotionBlock paragraph = children.get(0);
        assertType("paragraph");

        var richText = paragraph.getRichText();
        assertEquals(1, richText.size());

        assertNotNull(richText.get(0));
    }

    @Test
    public void testBulletList() {
        // TODO: reimplement with new API
    }

    @Test
    public void testCodeBlock() {
        // TODO: reimplement with new API
    }

    @Test
    public void testHeadings() {
        // TODO: reimplement with new API
    }

    @Test
    public void testNestedBulletList() {
        // TODO: reimplement with new API
    }

    @Test
    public void testOrderedList() {
        // TODO: reimplement with new API
    }

    @Test
    public void testRichText() {
        // TODO: reimplement with new API
    }
    
    private void validateRichText(JsonObject richText) {
        assertNotNull(richText);

        String type = getString(richText.get("type"));
        assertEquals("text", type);

        JsonObject text = getObject(richText.get(type));
        assertTrue(text.has("content"));
        
        // Ignore annotations for now, they are optional
    }

    private JsonObject getObject(JsonElement elem) {
        assertNotNull(elem);
        return elem.getAsJsonObject();
    }

    private JsonArray getArray(JsonElement elem) {
        assertNotNull(elem);
        return elem.getAsJsonArray();
    }

    private JsonPrimitive getPrimitive(JsonElement elem) {
        assertNotNull(elem);
        return elem.getAsJsonPrimitive();
    }

    private String getString(JsonElement elem) {
        return getPrimitive(elem).getAsString();
    }
}
