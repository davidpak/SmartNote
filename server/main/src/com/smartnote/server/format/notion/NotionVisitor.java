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
    private boolean onlyText;

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
        Block oldBlock = this.block;

        Block block = new Block(this.listStack.peek());
        this.block.addChild(block);

        this.block = block;

        //this.listStack.push("bulleted_list_item");

        visitChildren(bulletList);

        //this.listStack.pop();

        this.block = oldBlock;
    }

    @Override
    public void visit(Code code) {
        visitChildren(code);
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
        visitChildren(fencedCodeBlock);
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
        visitChildren(link);
    }

    @Override
    public void visit(ListItem listItem) {
        Block oldBlock = this.block;
        boolean oldOnlyText = this.onlyText;

        Block block = new Block(this.listStack.peek());
        this.block.addChild(block);

        this.block = block;
        this.onlyText = true;
        visitChildren(listItem);
        this.block = oldBlock;
        this.onlyText = oldOnlyText;
    }

    @Override
    public void visit(OrderedList orderedList) {
        this.listStack.push("numbered_list_item");
        visitChildren(orderedList);
        this.listStack.pop();
    }

    @Override
    public void visit(Paragraph paragraph) {
        if (onlyText) {
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

        JsonObject textDataObject = new JsonObject();
        textDataObject.addProperty("content", text.getLiteral());

        textObject.add("text", textDataObject);

        styleStack.peek().addToText(textObject);

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
        private String type, internalType;
        private Block parent;

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
            this.internalType = type;
            this.richText = new ArrayList<>();
            this.children = new ArrayList<>();
            this.more = new JsonObject();
        }

        public void addRichText(JsonObject object) {
            richText.add(object);
        }

        public void addChild(Block block) {
            children.add(block);
            block.parent = this;
        }

        public JsonObject createJson() {
            JsonObject json = new JsonObject();

            if (object != null)
                json.addProperty("object", "block");

            if (type != null)
                json.addProperty("type", type);

            if (richText.size() > 0) {
                JsonArray richTextArray = new JsonArray();
                for (JsonObject richTextObject : richText)
                    richTextArray.add(richTextObject);
      
                JsonObject objectData = new JsonObject();
                objectData.add("rich_text", richTextArray);

                json.add(type, objectData);
            }

            if (children.size() > 0) {
                JsonArray childrenArray = new JsonArray();
                for (Block child : children)
                    childrenArray.add(child.createJson());
                json.add("children", childrenArray);
            }

            for (String key : more.keySet())
                json.add(key, more.get(key));

            return json;
        }
    }
    
    private static class Style {
        private boolean bold;
        private boolean italic;
        private boolean strikethrough;
        private boolean underline;
        private boolean code;
        
        public Style() {
            this.bold = false;
            this.italic = false;
            this.strikethrough = false;
            this.underline = false;
            this.code = false;
        }

        public Style(Style style) {
            this.bold = style.bold;
            this.italic = style.italic;
            this.strikethrough = style.strikethrough;
            this.underline = style.underline;
            this.code = style.code;
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
    }
}
