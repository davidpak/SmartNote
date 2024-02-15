package com.smartnote.testing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.smartnote.server.format.ParsedMarkdown;

public class BaseMarkdown extends Base {
    public static final Path TEST_FILES_DIR = Paths.get("server", "testfiles", "markdown");

    public static final String BASIC_TEXT = "basic_text";
    public static final String BULLET_LIST = "bullet_list";
    public static final String CODE_BLOCK = "code_block";
    public static final String HEADINGS = "headings";
    public static final String NESTED_BULLET_LIST = "nested_bullet_list";
    public static final String ORDERED_LIST = "ordered_list";
    public static final String RICH_TEXT = "rich_text";

    private Map<String, String> files;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        this.files = new HashMap<>();

        Files.list(TEST_FILES_DIR).forEach(path -> {
            String name = path.getFileName().toString();
            if (!name.endsWith(".md"))
                return;

            name = name.substring(0, name.length() - 3);
            
            try {
                files.put(name, Files.readString(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public ParsedMarkdown parseMarkdown(String name) {
        return ParsedMarkdown.parse(files.get(name));
    }
}
