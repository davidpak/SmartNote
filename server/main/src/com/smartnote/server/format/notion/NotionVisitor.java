package com.smartnote.server.format.notion;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.*;

import com.google.gson.*;
import com.smartnote.server.util.JSONSerializable;

class NotionVisitor extends AbstractVisitor {
    private static final String DEFAULT_COLOR = "default";
    private static final String BLUE_COLOR = "blue";
    private static final String BLUE_BACKGROUND = "blue_background";
    private static final String BROWN_COLOR = "brown";
    private static final String BROWN_BACKGROUND = "brown_background";
    private static final String GRAY_COLOR = "gray";
    private static final String GRAY_BACKGROUND = "gray_background";
    private static final String GREEN_COLOR = "green";
    private static final String GREEN_BACKGROUND = "green_background";
    private static final String ORANGE_COLOR = "orange";
    private static final String ORANGE_BACKGROUND = "orange_background";
    private static final String PINK_COLOR = "pink";
    private static final String PINK_BACKGROUND = "pink_background";
    private static final String PURPLE_COLOR = "purple";
    private static final String PURPLE_BACKGROUND = "purple_background";
    private static final String RED_COLOR = "red";
    private static final String RED_BACKGROUND = "red_background";
    private static final String YELLOW_COLOR = "yellow";
    private static final String YELLOW_BACKGROUND = "yellow_background";

    private JsonObject root;
    private JsonArray children;

    public NotionVisitor(JsonObject root) {
        this.root = root;
        this.children = new JsonArray();
        this.root.add("children", this.children);
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        visitChildren(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        visitChildren(bulletList);
    }

    @Override
    public void visit(Code code) {
        visitChildren(code);
    }

    @Override
    public void visit(Document document) {
        visitChildren(document);
    }

    @Override
    public void visit(Emphasis emphasis) {
        visitChildren(emphasis);
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
        NotionHeading notionHeading = new NotionHeading();
        notionHeading.level = heading.getLevel();
        
        children.add(notionHeading.writeJSON());

        visitChildren(heading);
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
        visitChildren(link);
    }

    @Override
    public void visit(ListItem listItem) {
        visitChildren(listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        visitChildren(orderedList);
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
    public void visit(StrongEmphasis strongEmphasis) {
        visitChildren(strongEmphasis);
    }

    @Override
    public void visit(Text text) {
        visitChildren(text);
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
    
    private static abstract class NotionObject implements JSONSerializable {
        private final List<NotionObject> children = new ArrayList<>();

        void addChild(NotionObject child) {
            children.add(child);
        }

        abstract String getType();

        @Override
        public void writeJSON(JsonObject object) {
            if (children.size() > 0) {
                JsonArray childrenArray = new JsonArray();
                children.stream().map(NotionObject::writeJSON).forEach(childrenArray::add);
                object.add("children", childrenArray);
            }
        }

        @Override
        public void loadJSON(JsonObject object) {
            throw new UnsupportedOperationException();
        }
    }

    private static abstract class NotionRichText extends NotionObject {
        // annotations
        boolean bold;
        boolean italic;
        boolean strikethrough;
        boolean underline;
        boolean code;
        String color = DEFAULT_COLOR;

        // other fields
        String plainText;
        String href;

        @Override
        public void writeJSON(JsonObject object) {
            JsonObject annotations = createAnnotationsObject();
            if (annotations != null)
                object.add("annotations", annotations);

            if (plainText != null)
                object.addProperty("plain_text", plainText);

            if (href != null)
                object.addProperty("href", href);

            super.writeJSON(object);
        }
    
        private JsonObject createAnnotationsObject() {
            JsonObject annotationsObject = new JsonObject();
            if (bold) annotationsObject.addProperty("bold", bold);
            if (italic) annotationsObject.addProperty("italic", italic);
            if (strikethrough) annotationsObject.addProperty("strikethrough", strikethrough);
            if (underline) annotationsObject.addProperty("underline", underline);
            if (code) annotationsObject.addProperty("code", code);
            if (!color.equals(DEFAULT_COLOR)) annotationsObject.addProperty("color", color);
            return annotationsObject.size() > 0 ? annotationsObject : null;
        }
    }

    private static class NotionText extends NotionRichText {
        // text
        String content;
        String link;

        @Override
        public String getType() {
            return "text";
        }

        @Override
        public void writeJSON(JsonObject object) {
            object.addProperty("type", "text");

            JsonObject textObject = new JsonObject();
            textObject.addProperty("content", content);
            textObject.addProperty("link", link);
            object.add("text", textObject);

            super.writeJSON(object);
        }
    }

    private static abstract class NotionRichTextArray extends NotionObject {
        private JsonObject internalObject;
        private final JsonArray richText = new JsonArray();
        String color = DEFAULT_COLOR;

        void add(NotionRichText richText) {
            this.richText.add(richText.writeJSON());
        }

        @Override
        public void writeJSON(JsonObject object) {
            String type = getType();

            object.addProperty("type", type);

            internalObject = new JsonObject();
            internalObject.add("rich_text", richText);
            
            if (!color.equals(DEFAULT_COLOR))
                internalObject.addProperty("color", color);
            object.add(type, internalObject);
        }
        
        protected JsonObject getInternalObject() {
            return internalObject;
        }
    }

    private static class NotionParagraph extends NotionRichTextArray {
        @Override
        public String getType() {
            return "paragraph";
        }
    }

    private static class NotionHeading extends NotionRichTextArray {
        int level;
        boolean isToggleable;
    
        @Override
        public void writeJSON(JsonObject object) {
            super.writeJSON(object);

            if (isToggleable)
                getInternalObject().addProperty("is_toggleable", isToggleable);
        }

        @Override
        public String getType() {
            return "heading_" + level;
        }
    }

    private static class NotionBulletedList extends NotionRichTextArray {
        @Override
        public String getType() {
            return "bulleted_list_item";
        }
    }

    private static class NotionNumberedList extends NotionRichTextArray {
        @Override
        public String getType() {
            return "numbered_list_item";
        }
    }

    private static class NotionCode extends NotionObject {
        String language;
        private final JsonArray caption = new JsonArray();
        private final JsonArray code = new JsonArray();

        void addCaption(NotionRichText richText) {
            caption.add(richText.writeJSON());
        }

        void addCode(NotionRichText richText) {
            code.add(richText.writeJSON());
        }

        @Override
        public void writeJSON(JsonObject object) {
            object.addProperty("type", "code");

            JsonObject codeObject = new JsonObject();
            codeObject.add("caption", caption);
            codeObject.add("code", code);
            codeObject.addProperty("language", language);
            object.add("code", codeObject);

            super.writeJSON(object);
        }

        @Override
        public String getType() {
            return "code";
        }
    }
}
