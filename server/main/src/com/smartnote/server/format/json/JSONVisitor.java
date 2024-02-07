package com.smartnote.server.format.json;

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

    /**
     * Creates a new JSONVisitor.
     * 
     * @param root The root JSON object to write to.
     */
    public JSONVisitor(JsonObject root) {
        this.json = root;
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        type("blockQuote");
        visitChildren(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        type("bulletList");
        json.addProperty("bulletMarker", bulletList.getBulletMarker());
        visitChildren(bulletList);
    }

    @Override
    public void visit(Code code) {
        type("code");
        literal(code.getLiteral());
        visitChildren(code);
    }

    @Override
    public void visit(Document document) {
        type("document");
        visitChildren(document);
    }

    @Override
    public void visit(Emphasis emphasis) {
        type("emphasis");
        openingDelimeter(emphasis.getOpeningDelimiter());
        closingDelimeter(emphasis.getClosingDelimiter());
        visitChildren(emphasis);
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        type("fencedCodeBlock");

        json.addProperty("fenceChar", fencedCodeBlock.getFenceChar());
        json.addProperty("fenceLength", fencedCodeBlock.getFenceLength());
        json.addProperty("fenceIndent", fencedCodeBlock.getFenceIndent());

        json.addProperty("info", fencedCodeBlock.getInfo());

        literal(fencedCodeBlock.getLiteral());

        visitChildren(fencedCodeBlock);
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
        type("htmlInline");
        literal(htmlInline.getLiteral());
        visitChildren(htmlInline);
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        type("htmlBlock");
        literal(htmlBlock.getLiteral());
        visitChildren(htmlBlock);
    }

    @Override
    public void visit(Image image) {
        type("image");
        destination(image.getDestination());
        title(image.getTitle());
        visitChildren(image);
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        type("indentedCodeBlock");
        literal(indentedCodeBlock.getLiteral());
        visitChildren(indentedCodeBlock);
    }

    @Override
    public void visit(Link link) {
        type("link");
        destination(link.getDestination());
        title(link.getTitle());
        visitChildren(link);
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
        delimeter(orderedList.getDelimiter());
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
        type("strongEmphasis");
        openingDelimeter(strongEmphasis.getOpeningDelimiter());
        closingDelimeter(strongEmphasis.getClosingDelimiter());
        visitChildren(strongEmphasis);
    }

    @Override
    public void visit(Text text) {
        type("text");
        literal(text.getLiteral());
        visitChildren(text);
    }

    @Override
    public void visit(LinkReferenceDefinition linkReferenceDefinition) {
        type("linkReferenceDefinition");
        label(linkReferenceDefinition.getLabel());
        destination(linkReferenceDefinition.getDestination());
        title(linkReferenceDefinition.getTitle());
        visitChildren(linkReferenceDefinition);
    }

    @Override
    public void visit(CustomBlock customBlock) {
        type("customBlock");
        visitChildren(customBlock);
    }

    @Override
    public void visit(CustomNode customNode) {
        type("customNode");
        visitChildren(customNode);
    }

    @Override
    public void visitChildren(Node parent) {
        JsonObject saved = json;

        JsonArray children = new JsonArray();

        Node node = parent.getFirstChild();
        while (node != null) {
            json = new JsonObject();
            children.add(json);

            Node next = node.getNext();
            node.accept(this);
            node = next;
        }

        json = saved;

        if (children.size() > 0)
            json.add("children", children);
    }

    // Commonly added properties

    private void type(String type) {
        json.addProperty("type", type);
    }

    private void literal(String literal) {
        json.addProperty("literal", literal);
    }

    private void delimeter(char delimeter) {
        json.addProperty("delimeter", delimeter);
    }

    private void openingDelimeter(String openingDelimeter) {
        json.addProperty("openingDelimeter", openingDelimeter);
    }

    private void closingDelimeter(String closingDelimeter) {
        json.addProperty("closingDelimeter", closingDelimeter);
    }

    private void label(String label) {
        json.addProperty("label", label);
    }

    private void destination(String destination) {
        json.addProperty("destination", destination);
    }

    private void title(String title) {
        json.addProperty("title", title);
    }
}
