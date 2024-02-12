package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.commonmark.node.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * <p>
 * Converts markdown to Notion's internal format.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRenderer
 */
class NotionVisitor extends AbstractVisitor {
    private Block block;

    private Stack<String> listStack;
    private Stack<Style> styleStack;

    /**
     * Constructs a new NotionVisitor.
     * 
     * @param page The page to write to.
     */
    public NotionVisitor() {
        this.listStack = new Stack<>();
        this.styleStack = new Stack<>();
    }

    public JsonObject getJson() {
        return block.createJson();
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        visitChildren(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        this.listStack.push("bulleted_list_item");
        visitChildren(bulletList);
        this.listStack.pop();
    }

    @Override
    public void visit(Code code) {
        this.styleStack.push(styleStack.peek().code());
        Text text = new Text(code.getLiteral());
        visit(text);
        this.styleStack.pop();
    }

    @Override
    public void visit(Document document) {
        this.block = new Block();
        this.styleStack.push(new Style());
        visitChildren(document);
        this.styleStack.pop();
    }

    @Override
    public void visit(Emphasis emphasis) {
        this.styleStack.push(styleStack.peek().italic());
        visitChildren(emphasis);
        this.styleStack.pop();
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        Block oldBlock = this.block;
        String language = fencedCodeBlock.getInfo();
        if (language == null || language.length() == 0)
            language = "plain text";

        Block block = new Block("code");
        this.block.addChild(block);

        this.block = block;

        block.more.addProperty("language", language);

        Text text = new Text(fencedCodeBlock.getLiteral());
        visit(text);

        this.block = oldBlock;
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
    }

    @Override
    public void visit(Heading heading) {
        Block oldBlock = this.block;

        Block block = new Block("heading_" + heading.getLevel());
        this.block.addChild(block);

        this.block = block;
        visitChildren(heading);
        this.block = oldBlock;
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        // Ignore
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        // Ignore
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        // Ignore
    }

    @Override
    public void visit(Image image) {
        // Ignore
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        visitChildren(indentedCodeBlock);
    }

    @Override
    public void visit(Link link) {
        this.styleStack.push(styleStack.peek().link(link.getDestination()));
        visitChildren(link);
        this.styleStack.pop();
    }

    @Override
    public void visit(ListItem listItem) {
        Block oldBlock = this.block;

        Block block = new Block(this.listStack.peek());
        this.block.addChild(block);

        this.block = block;
        visitChildren(listItem);
        this.block = oldBlock;
    }

    @Override
    public void visit(OrderedList orderedList) {
        this.listStack.push("numbered_list_item");
        visitChildren(orderedList);
        this.listStack.pop();
    }

    @Override
    public void visit(Paragraph paragraph) {
        if (listStack.size() > 0) {
            visitChildren(paragraph);
            return;
        }

        Block oldBlock = this.block;

        Block block = new Block("paragraph");
        this.block.addChild(block);

        this.block = block;
        visitChildren(paragraph);
        this.block = oldBlock;
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {

    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        this.styleStack.push(styleStack.peek().bold());
        visitChildren(strongEmphasis);
        this.styleStack.pop();
    }

    @Override
    public void visit(Text text) {
        JsonObject textObject = new JsonObject();
        textObject.addProperty("type", "text");

        Style style = new Style(styleStack.peek());

        JsonObject textDataObject = new JsonObject();
        textDataObject.addProperty("content", text.getLiteral());

        if (style.link != null)
            textObject.addProperty("link", style.link);

        textObject.add("text", textDataObject);

        style.addToText(textObject);

        block.addRichText(textObject);
    }

    @Override
    public void visit(LinkReferenceDefinition linkReferenceDefinition) {
        // Ignore
    }

    @Override
    public void visit(CustomBlock customBlock) {
        visitChildren(customBlock);
    }

    @Override
    public void visit(CustomNode customNode) {
        visitChildren(customNode);
    }

    private static class Block {
        private String object;
        private String type;

        private List<JsonObject> richText;
        private List<Block> children;

        private JsonObject more;

        public Block() {
            this(null, null);
        }

        public Block(String type) {
            this("block", type);
        }

        public Block(String object, String type) {
            this.object = object;
            this.type = type;
            this.richText = new ArrayList<>();
            this.children = new ArrayList<>();
            this.more = new JsonObject();
        }

        public void addRichText(JsonObject object) {
            richText.add(object);
        }

        public void addChild(Block block) {
            children.add(block);
        }

        public JsonObject createJson() {
            JsonObject json = new JsonObject();

            if (object != null)
                json.addProperty("object", "block");

            if (type != null)
                json.addProperty("type", type);

            JsonObject objectData = new JsonObject();

            if (richText.size() > 0) {
                JsonArray richTextArray = new JsonArray();
                for (JsonObject richTextObject : richText)
                    richTextArray.add(richTextObject);
                objectData.add("rich_text", richTextArray);
            }

            if (children.size() > 0) {
                JsonArray childrenArray = new JsonArray();
                for (Block child : children)
                    childrenArray.add(child.createJson());

                if (type != null)
                    objectData.add("children", childrenArray);
                else
                    json.add("children", childrenArray);
            }

            for (String key : more.keySet())
                objectData.add(key, more.get(key));

            if (type != null && objectData.size() > 0)
                json.add(type, objectData);

            return json;
        }
    }

    private static class Style {
        private boolean bold;
        private boolean italic;
        private boolean strikethrough;
        private boolean underline;
        private boolean code;

        private String link;

        public Style() {
            this.bold = false;
            this.italic = false;
            this.strikethrough = false;
            this.underline = false;
            this.code = false;
            this.link = null;
        }

        public Style(Style style) {
            this.bold = style.bold;
            this.italic = style.italic;
            this.strikethrough = style.strikethrough;
            this.underline = style.underline;
            this.code = style.code;
            this.link = style.link;
        }

        public void addToText(JsonObject textJson) {
            JsonObject json = new JsonObject();

            if (bold)
                json.addProperty("bold", true);

            if (italic)
                json.addProperty("italic", true);

            if (strikethrough)
                json.addProperty("strikethrough", true);

            if (underline)
                json.addProperty("underline", true);

            if (code)
                json.addProperty("code", true);

            if (json.size() > 0)
                textJson.add("annotations", json);
        }

        public Style bold() {
            Style style = new Style(this);
            style.bold = true;
            return style;
        }

        public Style italic() {
            Style style = new Style(this);
            style.italic = true;
            return style;
        }

        public Style strikethrough() {
            Style style = new Style(this);
            style.strikethrough = true;
            return style;
        }

        public Style underline() {
            Style style = new Style(this);
            style.underline = true;
            return style;
        }

        public Style code() {
            Style style = new Style(this);
            style.code = true;
            return style;
        }

        public Style link(String link) {
            Style style = new Style(this);
            style.link = link;
            return style;
        }
    }
}
