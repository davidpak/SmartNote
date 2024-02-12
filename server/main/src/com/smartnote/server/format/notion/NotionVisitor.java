package com.smartnote.server.format.notion;

import static com.smartnote.server.format.notion.RichTextData.*;

import java.util.function.Supplier;

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
    private NotionPage page;
    private NotionBlock current;

    /**
     * The rich text data supplier. Used to specify how to generate
     * rich text data. The <code>RichTextData</code> class has
     * static methods that return suppliers to generate rich text.
     * These suppliers are chained together to apply multiple
     * styles to the same text.
     * 
     * @see RichTextData
     */
    private Supplier<RichTextData> richText;

    private JsonObject json;
    private JsonArray array;

    /**
     * Constructs a new NotionVisitor.
     * 
     * @param page The page to write to.
     */
    public NotionVisitor(NotionPage page) {
        this.page = page;
        this.json = new JsonObject();
    }

    public JsonObject getJson() {
        return json;
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        emit(new NotionQuote(), blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        emit(new NotionBulletedList(), bulletList);
    }

    @Override
    public void visit(Code code) {
        next(setCode(richText), code);
    }

    @Override
    public void visit(Document document) {
        richText = () -> new RichTextData();

        array = new JsonArray();
        json.add("children", array);
        visitChildren(document);
    }

    @Override
    public void visit(Emphasis emphasis) {
        next(setItalic(richText), emphasis);
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        next(setCode(richText), fencedCodeBlock);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        emit(new NotionParagraph(), hardLineBreak);
    }

    @Override
    public void visit(Heading heading) {
        visitChildren(heading);
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
        next(setCode(richText), indentedCodeBlock);
    }

    @Override
    public void visit(Link link) {
        next(setHref(richText).apply(link.getDestination()), link);
    }

    @Override
    public void visit(ListItem listItem) {
        visitChildren(listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        emit(new NotionNumberedList(), orderedList);
    }

    @Override
    public void visit(Paragraph paragraph) {
        JsonArray oldArray = array;

        JsonObject paraObject = new JsonObject();
        paraObject.addProperty("object", "block");
        paraObject.addProperty("type", "paragraph");
        paraObject.add("paragraph", paraObject);

        JsonArray richTextArray = new JsonArray();
        paraObject.add("rich_text", richTextArray);

        array = richTextArray;

        visitChildren(paragraph);

        array = oldArray;

        // emit(new NotionParagraph(), paragraph);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        emit(new NotionParagraph(), softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        next(setBold(richText), strongEmphasis);
    }

    @Override
    public void visit(Text text) {

        JsonObject richTextObject = new JsonObject();
        richTextObject.addProperty("type", "text");

        JsonObject textObject = new JsonObject();
        textObject.addProperty("content", text.getLiteral());

        richTextObject.add("text", textObject);

        // emit(new NotionText(text.getLiteral(), richText.get()), text);
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

    /**
     * Add a block as a child of the current block or the page if there is no
     * current block.
     * 
     * @param block The block to add.
     */
    private void add(NotionBlock block) {
        if (current == null)
            page.getProperties().add(block);
        else
            current.add(block);
    }

    /**
     * Consume a node and visit its children. The <code>richText</code>
     * field will be set to the given supplier while the children are visited.
     * It is then reset to its previous value. This does not add a block
     * to the current block or page.
     * 
     * @param supplier A supplier that returns rich text data.
     * @param node     The node to consume.
     */
    private void next(Supplier<RichTextData> supplier, Node node) {
        Supplier<RichTextData> old = richText;
        richText = supplier;
        visitChildren(node);
        richText = old;
    }

    /**
     * Emit a block and visit its children. The current block is set to the
     * given block while the children are visited. It is then reset to its
     * previous value. This will add the block to the current block or page.
     * 
     * @param block The block to emit.
     * @param node  The node to visit.
     */
    private void emit(NotionBlock block, Node node) {
        System.out.println("Emit: " + node.getClass().getSimpleName());
        add(block);
        current = block;
        visitChildren(node);
        current = block.getParent();
    }
}
