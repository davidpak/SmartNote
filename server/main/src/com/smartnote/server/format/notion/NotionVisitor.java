package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.commonmark.node.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
     */
    public NotionVisitor() {
        this.listStack = new Stack<>();
        this.styleStack = new Stack<>();
    }

    /**
     * Create a Notion-compatible JSON object.
     * 
     * @return The JSON object.
     */
    public JsonObject createJson() {
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
        block.addRichText(code.getLiteral(), styleStack.peek().code());
    }

    @Override
    public void visit(Document document) {
        this.block = new Block(null);
        this.styleStack.push(new Style());
        visitChildren(document);
        this.styleStack.pop();
    }

    @Override
    public void visit(Emphasis emphasis) {
        visitChildren(emphasis, styleStack.peek().italic());
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        String language = fencedCodeBlock.getInfo();
        if (language == null || language.length() == 0)
            language = "plain text";

        Block block = new Block("code");
        block.addProperty("language", language);
        block.addRichText(fencedCodeBlock.getLiteral(), styleStack.peek());

        this.block.addChild(block);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        // Ignore
    }

    @Override
    public void visit(Heading heading) {
        visitChildren(heading, new Block("heading_" + heading.getLevel()));
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
        visitChildren(link, styleStack.peek().link(link.getDestination()));
    }

    @Override
    public void visit(ListItem listItem) {
        visitChildren(listItem, new Block(this.listStack.peek()));
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

        visitChildren(paragraph, new Block("paragraph"));
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        // Ignore
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        visitChildren(strongEmphasis, styleStack.peek().bold());
    }

    @Override
    public void visit(Text text) {
        block.addRichText(text.getLiteral(), styleStack.peek());
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

    private void visitChildren(Node node, Block block) {
        Block oldBlock = this.block;
        this.block.addChild(block);
        this.block = block;
        visitChildren(node);
        this.block = oldBlock;
    }

    private void visitChildren(Node node, Style style) {
        this.styleStack.push(style);
        visitChildren(node);
        this.styleStack.pop();
    }

    /**
     * <p>
     * Represents a block in Notion's internal format.
     * </p>
     * 
     * @author Ethan Vrhel
     */
    private static class Block {
        private String type;

        private List<JsonObject> richText;
        private List<Block> children;

        private JsonObject more;

        /**
         * Create a new block.
         * 
         * @param type The type of the block. If <code>null</code>,
         * the block is the root block.
         */
        public Block(String type) {
            this.type = type;
            this.richText = new ArrayList<>();
            this.children = new ArrayList<>();
            this.more = new JsonObject();
        }

        /**
         * Add rich text to the block.
         * 
         * @param literal The literal text.
         * @param style  The style of the text.
         */
        public void addRichText(String literal, Style style) {      
            JsonObject textObject = new JsonObject();
            textObject.addProperty("type", "text");

            JsonObject textDataObject = new JsonObject();
            textDataObject.addProperty("content", literal);

            if (style.link != null) {
                JsonObject linkObject = new JsonObject();
                linkObject.addProperty("url", style.link);
                textDataObject.add("link", linkObject);
            }

            textObject.add("text", textDataObject);

            style.addToText(textObject);

            richText.add(textObject);
        }

        /**
         * Add a child block to the block.
         * 
         * @param block The child block.
         */
        public void addChild(Block block) {
            children.add(block);
        }

        /**
         * Add an additional property to the block.
         * 
         * @param key  The key.
         * @param value The value.
         */
        public void addProperty(String key, String value) {
            more.addProperty(key, value);
        }

        /**
         * Convert the block to a Notion-compatible JSON object.
         * 
         * @return The JSON object.
         */
        public JsonObject createJson() {
            JsonObject objectData = createObjectData();
            if (type == null) // represents the root block
                return objectData;

            JsonObject json = new JsonObject();

            // add object and type
            json.addProperty("object", "block");
            json.addProperty("type", type); 
            
            // only add objectData if it has data
            if (objectData.size() > 0)
                json.add(type, objectData);

            return json;
        }

        private JsonObject createObjectData() {
            JsonObject objectData = new JsonObject();

            // add rich text
            if (richText.size() > 0) {
                JsonArray richTextArray = new JsonArray();
                for (JsonObject richTextObject : richText)
                    richTextArray.add(richTextObject);
                objectData.add("rich_text", richTextArray);
            }

            // add children
            if (children.size() > 0) {
                JsonArray childrenArray = new JsonArray();
                for (Block child : children)
                    childrenArray.add(child.createJson());
                objectData.add("children", childrenArray);
            }

            // add additional properties
            for (String key : more.keySet())
                objectData.add(key, more.get(key));

            return objectData;
        }
    }

    /**
     * Describe a style in Notion's internal format.
     * 
     * @author Ethan Vrhel
     */
    private static class Style {
        private boolean bold;
        private boolean italic;
        private boolean strikethrough;
        private boolean underline;
        private boolean code;

        private String link; // null if no link

        /**
         * Create a default style.
         */
        public Style() {
            this.bold = false;
            this.italic = false;
            this.strikethrough = false;
            this.underline = false;
            this.code = false;
            this.link = null;
        }

        /**
         * Copy a style.
         * 
         * @param style The style to copy.
         */
        public Style(Style style) {
            this.bold = style.bold;
            this.italic = style.italic;
            this.strikethrough = style.strikethrough;
            this.underline = style.underline;
            this.code = style.code;
            this.link = style.link;
        }

        /**
         * Add the style to a text JSON object.
         * 
         * @param textJson The text JSON object.
         */
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

        /**
         * Return a new style with bold enabled.
         * 
         * @return The new style.
         */
        public Style bold() {
            Style style = new Style(this);
            style.bold = true;
            return style;
        }

        /**
         * Return a new style with italic enabled.
         * 
         * @return The new style.
         */
        public Style italic() {
            Style style = new Style(this);
            style.italic = true;
            return style;
        }

        /**
         * Return a new style with strikethrough enabled.
         * 
         * @return The new style.
         */
        public Style strikethrough() {
            Style style = new Style(this);
            style.strikethrough = true;
            return style;
        }

        /**
         * Return a new style with underline enabled.
         * 
         * @return The new style.
         */
        public Style underline() {
            Style style = new Style(this);
            style.underline = true;
            return style;
        }

        /**
         * Return a new style with code enabled.
         * 
         * @return The new style.
         */
        public Style code() {
            Style style = new Style(this);
            style.code = true;
            return style;
        }

        /**
         * Return a new style with a link.
         * 
         * @param link The link.
         * @return The new style.
         */
        public Style link(String link) {
            Style style = new Style(this);
            style.link = link;
            return style;
        }
    }
}
