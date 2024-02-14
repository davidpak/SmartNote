package com.smartnote.server.format;

import java.util.Stack;

import org.commonmark.node.*;

import com.google.gson.*;

/**
 * <p>
 * Converts Markdown to JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see JSONRenderer
 */
class JSONVisitor extends AbstractVisitor {
    private JsonObject json;
    private Stack<Style> styleStack;

    /**
     * Creates a new JSONVisitor.
     * 
     * @param root The root JSON object to write to.
     */
    public JSONVisitor(JsonObject root) {
        this.json = root;
        this.styleStack = new Stack<Style>();
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        type("blockQuote");
        visitChildren(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        type("bulletList");
        visitChildren(bulletList);
    }

    @Override
    public void visit(Code code) {
        emitCode(code.getLiteral(), null);
    }

    @Override
    public void visit(Document document) {
        type("document");
        styleStack.push(new Style());
        visitChildren(document);
        styleStack.pop();
    }

    @Override
    public void visit(Emphasis emphasis) {
        applyStyle(emphasis, styleStack.peek().setItalic());
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        type("fencedCodeBlock");
        emitCode(fencedCodeBlock.getLiteral(), fencedCodeBlock.getInfo());
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        type("hardLineBreak");
        visitChildren(hardLineBreak);
    }

    @Override
    public void visit(Heading heading) {
        type("heading");
        json.addProperty("level", heading.getLevel());
        visitChildren(heading);
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        type("thematicBreak");
        visitChildren(thematicBreak);
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
        type("indentedCodeBlock");
        emitCode(indentedCodeBlock.getLiteral(), null);
    }

    @Override
    public void visit(Link link) {
        applyStyle(link, styleStack.peek().setHref(link.getDestination()));
    }

    @Override
    public void visit(ListItem listItem) {
        type("listItem");
        visitChildren(listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        type("orderedList");
        json.addProperty("startNumber", orderedList.getStartNumber());
        visitChildren(orderedList);
    }

    @Override
    public void visit(Paragraph paragraph) {
        type("paragraph");
        visitChildren(paragraph);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        type("softLineBreak");
        visitChildren(softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        applyStyle(strongEmphasis, styleStack.peek().setBold());
    }

    @Override
    public void visit(Text text) {
        emitLiteral(text.getLiteral());
    }

    @Override
    public void visit(LinkReferenceDefinition linkReferenceDefinition) {
        applyStyle(linkReferenceDefinition, styleStack.peek().setHref(linkReferenceDefinition.getDestination()));
    }

    @Override
    public void visit(CustomBlock customBlock) {
        // Ignore
    }

    @Override
    public void visit(CustomNode customNode) {
        // Ignore
    }

    public void applyStyle(Node parent, Style style) {
        styleStack.push(style);
        super.visitChildren(parent);
        styleStack.pop();
    }

    @Override
    public void visitChildren(Node parent) {
        JsonObject object = json;

        JsonArray children = new JsonArray();

        Node child = parent.getFirstChild();
        while (child != null) {
            json = new JsonObject();
            child.accept(this);
            children.add(json);
            child = child.getNext();
        }

        json = object;

        if (children.size() > 0)
            json.add("children", children);
    }

    private void type(String type) {
        json.addProperty("type", type);
    }

    private void emitLiteral(String literal) {
        json.addProperty("type", "text");
        json.addProperty("literal", literal);

        Style style = styleStack.peek();
        JsonObject styleObject = style.createJson();
        if (styleObject.size() > 0)
            json.add("style", styleObject);
    }
    
    private void emitCode(String literal, String language) {
        styleStack.push(styleStack.peek().setCode());
        emitLiteral(literal);
        if (language != null && !language.isEmpty())
            json.addProperty("language", language);
        styleStack.pop();
    }

    private static class Style {
        boolean bold;
        boolean italic;
        boolean code;

        String href;

        public Style() {
            bold = false;
            italic = false;
            code = false;
            href = null;
        }

        public Style(Style style) {
            bold = style.bold;
            italic = style.italic;
            code = style.code;
            href = style.href;
        }
        
        public JsonObject createJson() {
            JsonObject json = new JsonObject();
            if (bold)
                json.addProperty("bold", true);

            if (italic)
                json.addProperty("italic", true);

            if (code)
                json.addProperty("code", true);

            if (href != null)
                json.addProperty("href", href);

            return json;
        }

        public Style setBold() {
            Style style = new Style(this);
            style.bold = true;
            return style;
        }

        public Style setItalic() {
            Style style = new Style(this);
            style.italic = true;
            return style;
        }

        public Style setCode() {
            Style style = new Style(this);
            style.code = true;
            return style;
        }

        public Style setHref(String href) {
            Style style = new Style(this);
            style.href = href;
            return style;
        }
    }
}
