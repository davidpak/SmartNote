package com.smartnote.server.format.notion;

import static com.smartnote.server.format.notion.RichTextData.*;

import java.util.function.Supplier;

import org.commonmark.node.*;

import com.google.gson.*;
import com.smartnote.server.util.JSONObjectSerializable;

class NotionVisitor extends AbstractVisitor implements JSONObjectSerializable {
    private NotionPage page;
    private NotionBlock current;

    private Supplier<RichTextData> richText;

    public NotionVisitor() {
        this.page = new NotionPage();
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
        emit(new NotionParagraph(), paragraph);
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
        emit(new NotionText(text.getLiteral(), richText.get()), text);
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

    @Override
    public JsonObject writeJSON(JsonObject json) {
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    private void add(NotionBlock block) {
        if (current == null)
            page.add(block);
        else
            current.add(block);
    }

    private void next(Supplier<RichTextData> supplier, Node node) {
        Supplier<RichTextData> old = richText;
        richText = supplier;
        visitChildren(node);
        richText = old;
    }

    private void emit(NotionBlock block, Node node) {
        add(block);
        current = block;
        visitChildren(node);
        current = block.getParent();
    }
}
