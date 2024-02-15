package com.smartnote.server.format.notion;

import java.util.Stack;

import com.smartnote.server.format.MarkdownVisitor;
import com.smartnote.server.format.ParsedMarkdown;

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

    /**
     * Constructs a new NotionVisitor.
     */
    public NotionVisitor() {
        this.listStack = new Stack<>();
    }

    public NotionBlock getBlock() {
        return block;
    }

    @Override
    public void visitBulletList(ParsedMarkdown md) {
        this.listStack.push("bulleted_list_item");
        visitChildren(md);
        this.listStack.pop();
    }

    @Override
    public void visitDocument(ParsedMarkdown md) {
        this.block = new NotionBlock(null);
        visitChildren(md);
    }

    @Override
    public void visitFencedCodeBlock(ParsedMarkdown md) {
        String language = md.getLanguage();
        if (language == null || language.length() == 0)
            language = "plain text";

        NotionBlock block = new NotionBlock("code");
        block.addProperty("language", language);
        block.addRichText(md.getLiteral(), md.getStyle());

        this.block.addChild(block);
    }

    @Override
    public void visitHardLineBreak(ParsedMarkdown md) {
        // Ignore
    }

    @Override
    public void visitHeading(ParsedMarkdown md) {
        visitChildren(md, new NotionBlock("heading_" + md.getLevel()));
    }

    @Override
    public void visitThematicBreak(ParsedMarkdown md) {
        // Ignore
    }

    @Override
    public void visitIndentedCodeBlock(ParsedMarkdown md) {
        visitChildren(md);
    }

    @Override
    public void visitListItem(ParsedMarkdown md) {
        visitChildren(md, new NotionBlock(this.listStack.peek()));
    }

    @Override
    public void visitOrderedList(ParsedMarkdown md) { 
        this.listStack.push("numbered_list_item");
        visitChildren(md);
        this.listStack.pop();
    }

    @Override
    public void visitParagraph(ParsedMarkdown md) {
        if (listStack.size() > 0) {
            visitChildren(md);
            return;
        }

        visitChildren(md, new NotionBlock("paragraph"));
    }

    @Override
    public void visitSoftLineBreak(ParsedMarkdown md) {
        // Ignore
    }

    @Override
    public void visitText(ParsedMarkdown md) {
        block.addRichText(md.getLiteral(), md.getStyle());
    }
    
    private void visitChildren(ParsedMarkdown md, NotionBlock block) {
        NotionBlock oldBlock = this.block;
        this.block.addChild(block);
        this.block = block;
        visitChildren(md);
        this.block = oldBlock;
    }
}
