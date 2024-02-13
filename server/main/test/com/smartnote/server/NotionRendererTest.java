package com.smartnote.server;

import static org.junit.Assert.*;

import org.commonmark.node.Node;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.format.notion.NotionRenderer;
import com.smartnote.testing.BaseMarkdown;

/**
 * <p>Tests the NotionRenderer class.</p>
 * 
 * @author Ethan Vrhel
 */
public class NotionRendererTest extends BaseMarkdown {
    private NotionRenderer renderer;

    private JsonArray children;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        renderer = new NotionRenderer();
    }
    
    private JsonObject renderJson(String name) {
        Node document = parseMarkdown(name);
        JsonObject notionJson = renderer.renderJson(document);
        assertNotNull(notionJson);

        JsonElement children = notionJson.get("children");
        assertNotNull(children);
        assertTrue(children.isJsonArray());

        this.children = children.getAsJsonArray();

        return notionJson;
    }
    
    @Test
    public void testBasicText() {
        renderJson(BASIC_TEXT);
        assertEquals(1, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "paragraph", 1, 0);
    }

    @Test
    public void testBulletList() {
        renderJson(BULLET_LIST);
        assertEquals(3, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "bulleted_list_item", 1, 0);
        validateBlock(children.get(1).getAsJsonObject(), "bulleted_list_item", 1, 0);
        validateBlock(children.get(2).getAsJsonObject(), "bulleted_list_item", 1, 0);
    }

    @Test
    public void testCodeBlock() {
        renderJson(CODE_BLOCK);
        assertEquals(3, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "code", 1, 0);
        validateBlock(children.get(1).getAsJsonObject(), "code", 1, 0);
        validateBlock(children.get(2).getAsJsonObject(), "code", 1, 0);
    }

    @Test
    public void testHeadings() {
        renderJson(HEADINGS);
        assertEquals(3, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "heading_1", 1, 0);
        validateBlock(children.get(1).getAsJsonObject(), "heading_2", 1, 0);
        validateBlock(children.get(2).getAsJsonObject(), "heading_3", 1, 0);
    }

    @Test
    public void testNestedBulletList() {
        renderJson(NESTED_BULLET_LIST);
        assertEquals(2, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "bulleted_list_item", 1, 2);
        validateBlock(children.get(1).getAsJsonObject(), "bulleted_list_item", 1, 2);
    }

    @Test
    public void testOrderedList() {
        renderJson(ORDERED_LIST);
        assertEquals(3, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "numbered_list_item", 1, 0);
        validateBlock(children.get(1).getAsJsonObject(), "numbered_list_item", 1, 0);
        validateBlock(children.get(2).getAsJsonObject(), "numbered_list_item", 1, 0);
    }

    @Test
    public void testRichText() {
        renderJson(RICH_TEXT);
        assertEquals(2, children.size());
        validateBlock(children.get(0).getAsJsonObject(), "paragraph", 11, 0);
        validateBlock(children.get(1).getAsJsonObject(), "paragraph", 7, 0);
    }
    
    private void validateBlock(JsonObject block, String type, int numRichText, int numChildren) {
        String str;
        JsonObject obj;

        assertNotNull(block);

        str = getString(block.get("object"));
        assertEquals("block", str);

        str = getString(block.get("type"));
        if (type != null)
            assertEquals(type, str);

        obj = getObject(block.get(str));

        if (obj.has("rich_text")) {
            JsonArray richText = getArray(obj.get("rich_text"));
            assertEquals(numRichText, richText.size());

            for (JsonElement elem : richText) {
                JsonObject richTextObj = getObject(elem);
                validateRichText(richTextObj);
            }
        }

        if (numChildren > 0) {
            JsonArray children = getArray(obj.get("children"));
            assertEquals(numChildren, children.size());
        }
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
