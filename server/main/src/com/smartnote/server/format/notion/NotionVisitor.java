package com.smartnote.server.format.notion;

import java.util.Stack;

import com.smartnote.server.format.MarkdownVisitor;
import com.smartnote.server.format.nodes.BulletList;
import com.smartnote.server.format.nodes.Document;
import com.smartnote.server.format.nodes.FencedCodeBlock;
import com.smartnote.server.format.nodes.HardLineBreak;
import com.smartnote.server.format.nodes.Heading;
import com.smartnote.server.format.nodes.IndentedCodeBlock;
import com.smartnote.server.format.nodes.ListItem;
import com.smartnote.server.format.nodes.MarkdownNode;
import com.smartnote.server.format.nodes.OrderedList;
import com.smartnote.server.format.nodes.Paragraph;
import com.smartnote.server.format.nodes.SoftLineBreak;
import com.smartnote.server.format.nodes.Text;
import com.smartnote.server.format.nodes.ThematicBreak;

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

    /**
     * Retrieve the generated block.
     * 
     * @return The generated block.
     */
    public NotionBlock getBlock() {
        return block;
    }

    @Override
    public void visit(BulletList md) {
        this.listStack.push("bulleted_list_item");
        visitChildren(md);
        this.listStack.pop();
    }

    @Override
    public void visit(Document md) {
        this.block = new NotionBlock(null);
        visitChildren(md);
    }

    @Override
    public void visit(FencedCodeBlock md) {
        String language = md.getLanguage();
        if (language == null || language.length() == 0)
            language = "plain text";

        NotionBlock block = new NotionBlock("code");
        block.addProperty("language", language);
        block.addRichText(md.getLiteral(), md.getStyle());

        this.block.addChild(block);
    }

    @Override
    public void visit(HardLineBreak md) {
        // Ignore
    }

    @Override
    public void visit(Heading md) {
        visitChildren(md, new NotionBlock("heading_" + md.getLevel()));
    }

    @Override
    public void visit(ThematicBreak md) {
        // Ignore
    }

    @Override
    public void visit(IndentedCodeBlock md) {
        visitChildren(md);
    }

    @Override
    public void visit(ListItem md) {
        visitChildren(md, new NotionBlock(this.listStack.peek()));
    }

    @Override
    public void visit(OrderedList md) {
        this.listStack.push("numbered_list_item");
        visitChildren(md);
        this.listStack.pop();
    }

    @Override
    public void visit(Paragraph md) {
        if (listStack.size() > 0) {
            visitChildren(md);
            return;
        }

        visitChildren(md, new NotionBlock("paragraph"));
    }

    @Override
    public void visit(SoftLineBreak md) {
        // Ignore
    }

    @Override
    public void visit(Text md) {
        block.addRichText(md.getLiteral(), md.getStyle());
    }

    private void visitChildren(MarkdownNode md, NotionBlock block) {
        NotionBlock oldBlock = this.block;
        this.block.addChild(block);
        this.block = block;
        visitChildren(md);
        this.block = oldBlock;
    }
}
