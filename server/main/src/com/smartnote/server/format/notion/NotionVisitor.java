package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.commonmark.node.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.format.Style;
import com.smartnote.server.format.MarkdownVisitor;

/**
 * <p>
 * Converts markdown to Notion's internal format.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionRenderer
 */
class NotionVisitor extends MarkdownVisitor {
    private NotionBlock block;

    private Stack<String> listStack;
    private Stack<Style> styleStack;

    /**
     * Constructs a new NotionVisitor.
     */
    public NotionVisitor() {
        this.listStack = new Stack<>();
        this.styleStack = new Stack<>();
    }

    public NotionBlock getBlock() {
        return block;
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
        this.block = new NotionBlock(null);
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

        NotionBlock block = new NotionBlock("code");
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
        visitChildren(heading, new NotionBlock("heading_" + heading.getLevel()));
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
        visitChildren(listItem, new NotionBlock(this.listStack.peek()));
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
}
