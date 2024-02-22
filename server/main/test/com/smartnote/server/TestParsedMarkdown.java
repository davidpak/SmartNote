package com.smartnote.server;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.Style;
import com.smartnote.server.format.nodes.BulletList;
import com.smartnote.server.format.nodes.Document;
import com.smartnote.server.format.nodes.FencedCodeBlock;
import com.smartnote.server.format.nodes.Heading;
import com.smartnote.server.format.nodes.ListItem;
import com.smartnote.server.format.nodes.MarkdownNode;
import com.smartnote.server.format.nodes.OrderedList;
import com.smartnote.server.format.nodes.Paragraph;
import com.smartnote.server.format.nodes.Text;
import com.smartnote.testing.BaseMarkdown;

public class TestParsedMarkdown extends BaseMarkdown {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private Document getDocument(String name) {
        ParsedMarkdown md = parseMarkdown(name);
        assertNotNull(md);
        Document doc = md.getDocument();
        assertNotNull(doc);
        return doc;
    }

    private void assertDeepEquals(MarkdownNode expected, MarkdownNode actual) {
        assertDeepEquals(expected.writeJSON(), actual.writeJSON());
    }

    private void assertDeepEquals(JsonElement expected, JsonElement actual) {
        assertEquals(expected.getClass(), actual.getClass());

        if (expected.isJsonArray()) {
            assertDeepEquals((JsonArray) expected, (JsonArray) actual);
        } else if (expected.isJsonObject()) {
            assertDeepEquals((JsonObject) expected, (JsonObject) actual);
        } else {
            assertEquals(expected, actual);
        }
    }

    private void assertDeepEquals(JsonArray expected, JsonArray actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++)
            assertDeepEquals(expected.get(i), actual.get(i));
    }

    private void assertDeepEquals(JsonObject expected, JsonObject actual) {
        for (String key : expected.keySet()) {
            assertNotNull(actual.get(key));
            assertDeepEquals(expected.get(key), actual.get(key));
        }
    }

    private List<MarkdownNode> children(MarkdownNode... children) {
        return List.of(children);
    }

    private Text text(String literal) {
        return new Text(literal, null);
    }

    private Text text(String literal, Style style) {
        return new Text(literal, style);
    }

    private Paragraph paragraph(MarkdownNode... children) {
        return new Paragraph(children(children));
    }

    private Document document(MarkdownNode... children) {
        return new Document(children(children));
    }

    private ListItem listItem(MarkdownNode... children) {
        return new ListItem(children(children));
    }

    private BulletList bulletList(MarkdownNode... children) {
        return new BulletList(children(children));
    }

    private Heading heading(int level, MarkdownNode... children) {
        return new Heading(level, children(children));
    }

    private OrderedList orderedList(int startNumber, MarkdownNode... children) {
        return new OrderedList(startNumber, children(children));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotDocument() {
        String jsonString = "{\"type\":\"text\",\"literal\":\"Hello, world!\"}";
        JsonObject json = getGson().fromJson(jsonString, JsonObject.class);
        ParsedMarkdown.parseFromJson(json);
    }

    @Test
    public void testBasic() {
        // Expected
        Paragraph paragraph = paragraph(text("Hello World!"));
        Document expected = document(paragraph);

        var doc = getDocument(BASIC_TEXT);

        assertDeepEquals(expected, doc);
    }

    @Test
    public void testBulletList() {
        // Expected
        Paragraph item1Paragraph = paragraph(text("Item 1"));
        Paragraph item2Paragraph = paragraph(text("Item 2"));
        Paragraph item3Paragraph = paragraph(text("Item 3"));

        ListItem item1 = listItem(item1Paragraph);
        ListItem item2 = listItem(item2Paragraph);
        ListItem item3 = listItem(item3Paragraph);

        BulletList bulletList = bulletList(item1, item2, item3);

        Document expected = document(bulletList);

        var doc = getDocument(BULLET_LIST);

        assertDeepEquals(expected, doc);
    }

    @Test
    public void testCodeBlock() {
        // Expected
        FencedCodeBlock code1 = new FencedCodeBlock("javascript", "// JavaScript\nconsole.log(\"Hello, World!\");\n",
                null);
        FencedCodeBlock code2 = new FencedCodeBlock("python", "# Python\nprint(\"Hello, World!\")\n", null);
        FencedCodeBlock code3 = new FencedCodeBlock("c", "// C\nprintf(\"Hello, World!\\n\");\n", null);

        Document expected = document(code1, code2, code3);

        var doc = getDocument(CODE_BLOCK);

        assertDeepEquals(expected, doc);
    }

    @Test
    public void testHeadings() {
        // Expected
        Heading heading1 = heading(1, text("Heading 1"));
        Heading heading2 = heading(2, text("Heading 2"));
        Heading heading3 = heading(3, text("Heading 3"));

        Document expected = new Document(children(heading1, heading2, heading3));

        var doc = getDocument(HEADINGS);

        assertDeepEquals(expected, doc);
    }

    @Test
    public void testNestedBulletList() {
        // Expected
        Text item1Text = text("Item 1");
        Text item11Text = text("Item 1.1");
        Text item12Text = text("Item 1.2");

        Text item2Text = text("Item 2");
        Text item21Text = text("Item 2.1");
        Text item22Text = text("Item 2.2");

        ListItem item11 = listItem(paragraph(item11Text));
        ListItem item12 = listItem(paragraph(item12Text));
        BulletList bulletList1 = bulletList(item11, item12);

        ListItem item21 = listItem(paragraph(item21Text));
        ListItem item22 = listItem(paragraph(item22Text));
        BulletList bulletList2 = bulletList(item21, item22);

        ListItem item1 = listItem(paragraph(item1Text), bulletList1);
        ListItem item2 = listItem(paragraph(item2Text), bulletList2);

        BulletList bulletList = bulletList(item1, item2);

        Document expected = document(bulletList);

        var doc = getDocument(NESTED_BULLET_LIST);

        assertDeepEquals(expected, doc);
    }

    @Test
    public void testOrderedList() {
        // Expected
        Text item1Text = text("Item 1");
        Text item2Text = text("Item 2");
        Text item3Text = text("Item 3");

        ListItem item1 = listItem(paragraph(item1Text));
        ListItem item2 = listItem(paragraph(item2Text));
        ListItem item3 = listItem(paragraph(item3Text));

        Document expected = document(orderedList(1, item1, item2, item3));

        var doc = getDocument(ORDERED_LIST);

        assertDeepEquals(expected, doc);
    }

    @Test
    public void testRichText() {
        Style style = new Style();
        List<MarkdownNode> children = new ArrayList<>();

        children.add(text("Example ", style));
        children.add(text("rich", style.withItalic()));
        children.add(text(" ", style));
        children.add(text("text", style.withBold()));
        children.add(text(" with ", style));
        children.add(text("multiple", style.withItalic()));
        children.add(text(" ", style));
        children.add(text("styles", style.withBold()));
        children.add(text(" and ", style));
        children.add(text("links", style.withLink("https://www.notion.so/")));
        children.add(text(".", style));

        Paragraph para1 = new Paragraph(children);

        children.clear();

        children.add(text("A second paragraph with a ", style));
        children.add(text("link", style.withLink("https://www.notion.so/")));
        children.add(text(" and a ", style));
        children.add(text("second link", style.withLink("https://www.notion.so/")));
        children.add(text(". Even some ", style));
        children.add(text("inline code", style.withCode()));
        children.add(text("!", style));

        Paragraph para2 = new Paragraph(children);

        Document expected = document(para1, para2);

        var doc = getDocument(RICH_TEXT);

        assertDeepEquals(expected, doc);
    }
}
