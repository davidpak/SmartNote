package com.smartnote.server;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.format.Style;
import com.smartnote.server.format.notion.NotionBlock;
import com.smartnote.server.format.notion.NotionConverter;
import com.smartnote.server.format.notion.RichText;
import com.smartnote.testing.BaseMarkdown;

/**
 * <p>
 * Tests the NotionConverter class.
 * </p>
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

    private void assertDeepEquals(NotionBlock expected, NotionBlock actual) {
        assertEquals(expected.getType(), actual.getType());

        List<NotionBlock> expectedChildren = expected.getChildren();
        List<NotionBlock> actualChildren = actual.getChildren();

        assertEquals(expectedChildren.size(), actualChildren.size());

        for (int i = 0; i < expectedChildren.size(); i++)
            assertDeepEquals(expectedChildren.get(i), actualChildren.get(i));

        List<RichText> expectedRichText = expected.getRichText();
        List<RichText> actualRichText = actual.getRichText();

        assertEquals(expectedRichText.size(), actualRichText.size());

        for (int i = 0; i < expectedRichText.size(); i++)
            assertEquals(expectedRichText.get(i), actualRichText.get(i));

    }

    private NotionBlock block(String type) {
        return new NotionBlock(type);
    }

    private NotionBlock block(String type, String text, Style style) {
        NotionBlock block = block(type);
        block.addRichText(text, style);
        return block;
    }

    private NotionBlock block(String type, String text) {
        return block(type, text, null);
    }

    private NotionBlock paragraph() {
        return block("paragraph");
    }

    private NotionBlock paragraph(String text, Style style) {
        return block("paragraph", text, style);
    }

    private NotionBlock paragraph(String text) {
        return paragraph(text, null);
    }

    private NotionBlock bulletedListItem(String text) {
        return block("bulleted_list_item", text);
    }

    private NotionBlock orderedListItem(String text) {
        return block("numbered_list_item", text);
    }

    private NotionBlock code(String text, String language, Style style) {
        if (language == null || language.length() == 0)
            language = "plain text";

        NotionBlock block = block("code");
        block.addProperty("language", language);
        block.addRichText(text, style);

        return block;
    }

    private NotionBlock heading(String text, int level, Style style) {
        return block("heading_" + level, text, style);
    }

    @Test
    public void testBasicText() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        root.addChild(paragraph("Hello World!"));

        createBlock(BASIC_TEXT);

        assertDeepEquals(root, this.block);
    }

    @Test
    public void testBulletList() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        root.addChild(bulletedListItem("Item 1"));
        root.addChild(bulletedListItem("Item 2"));
        root.addChild(bulletedListItem("Item 3"));

        createBlock(BULLET_LIST);

        assertDeepEquals(root, this.block);
    }

    @Test
    public void testCodeBlock() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        root.addChild(code("// JavaScript\nconsole.log(\"Hello, World!\");\n", "javascript", null));
        root.addChild(code("# Python\nprint(\"Hello, World!\")\n", "python", null));
        root.addChild(code("// C\nprintf(\"Hello, World!\\n\");\n", "c", null));

        createBlock(CODE_BLOCK);

        assertDeepEquals(root, this.block);
    }

    @Test
    public void testHeadings() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        root.addChild(heading("Heading 1", 1, null));
        root.addChild(heading("Heading 2", 2, null));
        root.addChild(heading("Heading 3", 3, null));

        createBlock(HEADINGS);

        assertDeepEquals(root, this.block);
    }

    @Test
    public void testNestedBulletList() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        NotionBlock list1 = bulletedListItem("Item 1");
        list1.addChild(bulletedListItem("Item 1.1"));
        list1.addChild(bulletedListItem("Item 1.2"));
        root.addChild(list1);
        NotionBlock list2 = bulletedListItem("Item 2");
        list2.addChild(bulletedListItem("Item 2.1"));
        list2.addChild(bulletedListItem("Item 2.2"));
        root.addChild(list2);

        createBlock(NESTED_BULLET_LIST);

        assertDeepEquals(root, this.block);
    }

    @Test
    public void testOrderedList() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        root.addChild(orderedListItem("Item 1"));
        root.addChild(orderedListItem("Item 2"));
        root.addChild(orderedListItem("Item 3"));

        createBlock(ORDERED_LIST);

        assertDeepEquals(root, this.block);
    }

    @Test
    public void testRichText() {
        // Expected structure
        NotionBlock root = new NotionBlock(null);
        Style style = new Style();

        NotionBlock p1 = paragraph();
        p1.addRichText("Example ", style);
        p1.addRichText("rich", style.withItalic());
        p1.addRichText(" ", style);
        p1.addRichText("text", style.withBold());
        p1.addRichText(" with ", style);
        p1.addRichText("multiple", style.withItalic());
        p1.addRichText(" ", style);
        p1.addRichText("styles", style.withBold());
        p1.addRichText(" and ", style);
        p1.addRichText("links", style.withLink("https://www.notion.so/"));
        p1.addRichText(".", style);
        root.addChild(p1);

        NotionBlock p2 = paragraph();
        p2.addRichText("A second paragraph with a ", style);
        p2.addRichText("link", style.withLink("https://www.notion.so/"));
        p2.addRichText(" and a ");
        p2.addRichText("second link", style.withLink("https://www.notion.so/"));
        p2.addRichText(". Even some ", style);
        p2.addRichText("inline code", style.withCode());
        p2.addRichText("!", style);
        root.addChild(p2);

        createBlock(RICH_TEXT);

        assertDeepEquals(root, this.block);
    }
}
