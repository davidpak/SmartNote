package com.smartnote.server.format.rtf;

import java.util.Stack;

import org.commonmark.node.*;

import com.smartnote.server.util.SafeAppendable;

/**
 * <p>Converts Markdown to RTF.</p>
 * 
 * @author Ethan Vrhel
 * @see RTFRenderer
 */
class RTFVisitor extends AbstractVisitor {
    public static final String RTF_FONT = "\\fswiss\\fcharset0 Arial";
    public static final String RTF_MONO_FONT = "\\fmodern\\fcharset0 Courier New";

    public static final String RTF_HEADING_SIZES[] = {
        "\\fs48 ", "\\fs40 ", "\\fs32 ", "\\fs28 ", "\\fs24 ", "\\fs20 ", "\\fs18 ", "\\fs16 ", "\\fs15 ", "\\fs14 ", "\\fs13 ", "\\fs12 ", "\\fs11 ", "\\fs10 ", "\\fs9 ", "\\fs8 "
    };

    public static final String RTF_BODY_SIZE = "\\fs24 ";

    public static int LIST_ORDERED = 0;
    public static int LIST_UNORDERED = 1;

    private SafeAppendable rtf;
    private Stack<ListState> listStack;

    private static class ListState {
        final int type;
        int count;

        ListState(int type) {
            this.type = type;
            this.count = 1;
        }
    }

    /**
     * Creates a new RTF visitor.
     * 
     * @param output The <code>Appendable</code> to write to.
     */
    public RTFVisitor(Appendable output) {
        this.rtf = new SafeAppendable(output);
        this.listStack = new Stack<ListState>();
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        rtf.append("\\pard\\par ");
        visitChildren(blockQuote);
        rtf.append("\\par ");
    }

    @Override
    public void visit(BulletList bulletList) {
        listStack.push(new ListState(LIST_UNORDERED));
        visitChildren(bulletList);
        listStack.pop();
    }

    @Override
    public void visit(Code code) {
        rtf.append("{\\f1 ");
        styleSpecial(code);
        rtf.append(code.getLiteral());
        visitChildren(code);
        rtf.append("}");
    }

    @Override
    public void visit(Document document) {
        rtf.append("{\\rtf1\\ansi");

        String fonts = String.format("{\\fonttbl{\\f0%s;}{\\f1%s;}} ", RTF_FONT, RTF_MONO_FONT);
        rtf.append(fonts);

        String colors = "{\\colortbl;\\red0\\green0\\blue0;\\red255\\green0\\blue0;\\red0\\green255\\blue0;\\red0\\green0\\blue255;}";
        rtf.append(colors);

        visitChildren(document);
        rtf.append("}");
    }

    @Override
    public void visit(Emphasis emphasis) {
        rtf.append("{\\i ");
        visitChildren(emphasis);
        rtf.append("}");
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        rtf.append("{\\f1\\pard\\par ");
        appendVerbatim(fencedCodeBlock.getLiteral());
        visitChildren(fencedCodeBlock);
        rtf.append("}");
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        visitChildren(hardLineBreak);
        rtf.append("\\par ");
    }

    @Override
    public void visit(Heading heading) {
        visitChildren(heading);
        rtf.append("\\pard\\par ");
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        visitChildren(thematicBreak);
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        visitChildren(htmlInline);
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        visitChildren(htmlBlock);
    }

    @Override
    public void visit(Image image) {
        visitChildren(image);
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        visitChildren(indentedCodeBlock);
    }

    @Override
    public void visit(Link link) {
        String url = link.getDestination();

        String content = String.format("{\\field{\\*\\fldinst{HYPERLINK \"%s\"}}{\\fldrslt{\\cf4\\ul\\ulc4 ", url);
        rtf.append(content);

        visitChildren(link);

        rtf.append("}}}");
    }

    @Override
    public void visit(ListItem listItem) {
        ListState state = listStack.peek();
        int level = listStack.size();

        rtf.append("\\pard\\li");
        rtf.append(Integer.toString(level * 720));
        if (state.type == LIST_UNORDERED) {
            rtf.append("\\fi-720\\bullet\\tx720 ");
        } else if (state.type == LIST_ORDERED) {
            rtf.append("\\fi-720\\'b7\\'b7\\'b7\\tx720 ");
        } else {
            throw new IllegalStateException("Unknown list type: " + state.type);
        }

        state.count++;

        visitChildren(listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        listStack.push(new ListState(LIST_ORDERED));
        visitChildren(orderedList);
        listStack.pop();
    }

    @Override
    public void visit(Paragraph paragraph) {
        visitChildren(paragraph);
        rtf.append("\\par ");
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        visitChildren(softLineBreak);
        rtf.append("\\line ");
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        rtf.append("{\\b ");
        visitChildren(strongEmphasis);
        rtf.append("}");
    }

    @Override
    public void visit(Text text) {
        rtf.append("{\\f0");
        styleSpecial(text);
        rtf.append(text.getLiteral());
        visitChildren(text);
        rtf.append("}");
    }
    
    @Override
    public void visit(LinkReferenceDefinition linkReferenceDefinition) {
        visitChildren(linkReferenceDefinition);
    }

    @Override
    public void visit(CustomBlock customBlock) {
        visitChildren(customBlock);
    }

    @Override
    public void visit(CustomNode customNode) {
        visitChildren(customNode);
    }

    private void appendVerbatim(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\n':
                    rtf.append("\\par ");
                    break;
                case '\t':
                    rtf.append("\\tab ");
                    break;
                case '\\':
                    rtf.append("\\\\");
                    break;
                case '{':
                    rtf.append("\\{");
                    break;
                case '}':
                    rtf.append("\\}");
                    break;
                default:
                    rtf.append(c);
                    break;
            }
        }
    }

    private void styleSpecial(Node node) {
        Heading heading = null;
        Node parentNode = node.getParent();
        if (parentNode instanceof Heading) {
            heading = (Heading) parentNode;
            rtf.append("\\pard\\par\\b ");
            rtf.append(RTF_HEADING_SIZES[heading.getLevel() - 1]);
        }
    }
}
