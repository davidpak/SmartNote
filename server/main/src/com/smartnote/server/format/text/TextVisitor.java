package com.smartnote.server.format.text;

import com.smartnote.server.format.nodes.*;

import com.smartnote.server.format.MarkdownVisitor;
import com.smartnote.server.util.SafeAppendable;

public class TextVisitor extends MarkdownVisitor {
    private SafeAppendable rtf;
    private int tabLevel;

    private String bulletMarker;
    private int listItem;
    private boolean isOrdered;

    /**
     * Creates a new RTF visitor.
     * 
     * @param output The <code>Appendable</code> to write to.
     */
    public TextVisitor(Appendable output) {
        this.rtf = new SafeAppendable(output);
    }

    private void tab() {
        for (int i = 0; i < tabLevel; i++)
            rtf.append("    ");
    }

    private void println(Object o) {
        rtf.append(o.toString());
        rtf.append("\n");
    }

    private void printf(String format, Object... args) {
        rtf.append(String.format(format, args));
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        visitChildren(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        String oldMarker = bulletMarker;
        int oldListItem = listItem;
        boolean oldIsOrdered = isOrdered;

        bulletMarker = "*";
        listItem = 0;
        isOrdered = false;
        tabLevel++;

        visitChildren(bulletList);

        tabLevel--;
        bulletMarker = oldMarker;
        listItem = oldListItem;
        isOrdered = oldIsOrdered;
    }

    @Override
    public void visit(Document document) {
        visitChildren(document);
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        visitChildren(fencedCodeBlock);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        visitChildren(hardLineBreak);
    }

    @Override
    public void visit(Heading heading) {
        visitChildren(heading);
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        visitChildren(thematicBreak);
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        visitChildren(indentedCodeBlock);
    }

    @Override
    public void visit(ListItem listItem) {
        tab();
        if (isOrdered) {
            printf("%d. ", this.listItem++);
        } else {
            printf("%s ", bulletMarker);
        }
        visitChildren(listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        String oldMarker = bulletMarker;
        int oldListItem = listItem;
        boolean oldIsOrdered = isOrdered;

        bulletMarker = "1.";
        listItem = 1;
        isOrdered = true;
        tabLevel++;

        visitChildren(orderedList);

        tabLevel--;
        bulletMarker = oldMarker;
        listItem = oldListItem;
        isOrdered = oldIsOrdered;
    }

    @Override
    public void visit(Paragraph paragraph) {
        visitChildren(paragraph);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        visitChildren(softLineBreak);
    }

    @Override
    public void visit(Text text) {
        println(text.getLiteral());
    }
}
