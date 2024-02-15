package com.smartnote.server.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Contains utilities for working with MIME types.</p>
 * 
 * @author Ethan Vrhel
 */
public class MIME {
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types

    // Text MIME types
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_CSS = "text/css";
    public static final String TEXT_JS = "application/javascript";
    public static final String TEXT_JSON = "application/json";
    public static final String TEXT_XML = "application/xml";
    public static final String TEXT_CSV = "text/csv";
    public static final String TEXT_PHP = "application/x-httpd-php";
    public static final String TEXT_XHTML = "application/xhtml+xml";
    public static final String TEXT_MARKDOWN = "text/markdown";

    // Document MIME types
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String APPLICATION_PPT = "application/vnd.ms-powerpoint";
    public static final String APPLICATION_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String APPLICATION_DOC = "application/msword";
    public static final String APPLICATION_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String APPLICATION_XLS = "application/vnd.ms-excel";

    // Media MIME types
    public static final String AUDIO_MP3 = "audio/mpeg";
    public static final String AUDIO_WAV = "audio/wav";
    public static final String AUDIO_OGG = "audio/ogg";
    public static final String AUDIO_OPUS = "audio/opus";
    public static final String AUDIO_FLAC = "audio/flac";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_OGG = "video/ogg";
    public static final String VIDEO_WEBM = "video/webm";
    public static final String VIDEO_AVI = "video/x-msvideo";
    public static final String VIDEO_MPEG = "video/mpeg";

    // Images
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_SVG = "image/svg+xml";
    public static final String IMAGE_BMP = "image/bmp";
    public static final String IMAGE_ICO = "image/x-icon";
    public static final String IMAGE_TIFF = "image/tiff";
    public static final String IMAGE_WEBP = "image/webp";
    public static final String IMAGE_APNG = "image/apng";

    // Archives
    public static final String APPLICATION_ZIP = "application/zip";
    public static final String APPLICATION_TAR = "application/x-tar";
    public static final String APPLICATION_GZ = "application/gzip";
    public static final String APPLICATION_BZ = "application/x-bzip";

    // Binary
    public static final String APPLICATION_EXE = "application/x-msdownload";
    public static final String APPLICATION_BIN = "application/octet-stream";
    public static final String APPLICATION_JAR = "application/java-archive";

    // Shorthand constants
    public static final String TEXT = TEXT_PLAIN;
    public static final String HTML = TEXT_HTML;
    public static final String CSS = TEXT_CSS;
    public static final String JS = TEXT_JS;
    public static final String JSON = TEXT_JSON;
    public static final String XML = TEXT_XML;
    public static final String CSV = TEXT_CSV;
    public static final String PHP = TEXT_PHP;
    public static final String XHTML = TEXT_XHTML;
    public static final String MARKDOWN = TEXT_MARKDOWN;
    public static final String PDF = APPLICATION_PDF;
    public static final String PPTX = APPLICATION_PPTX;
    public static final String PPT = APPLICATION_PPT;
    public static final String DOCX = APPLICATION_DOCX;
    public static final String DOC = APPLICATION_DOC;
    public static final String XLSX = APPLICATION_XLSX;
    public static final String XLS = APPLICATION_XLS;
    public static final String MP3 = AUDIO_MP3;
    public static final String WAV = AUDIO_WAV;
    public static final String OGG = AUDIO_OGG;
    public static final String OPUS = AUDIO_OPUS;
    public static final String FLAC = AUDIO_FLAC;
    public static final String MP4 = VIDEO_MP4;
    public static final String OGV = VIDEO_OGG;
    public static final String WEBM = VIDEO_WEBM;
    public static final String AVI = VIDEO_AVI;
    public static final String MPEG = VIDEO_MPEG;
    public static final String PNG = IMAGE_PNG;
    public static final String JPEG = IMAGE_JPEG;
    public static final String GIF = IMAGE_GIF;
    public static final String SVG = IMAGE_SVG;
    public static final String BMP = IMAGE_BMP;
    public static final String ICO = IMAGE_ICO;
    public static final String TIFF = IMAGE_TIFF;
    public static final String WEBP = IMAGE_WEBP;
    public static final String APNG = IMAGE_APNG;
    public static final String ZIP = APPLICATION_ZIP;
    public static final String TAR = APPLICATION_TAR;
    public static final String GZ = APPLICATION_GZ;
    public static final String BZ = APPLICATION_BZ;
    public static final String EXE = APPLICATION_EXE;
    public static final String BIN = APPLICATION_BIN;
    public static final String JAR = APPLICATION_JAR;

    // MIME -> common extension (not comprehensive)
    private static final Map<String, Set<String>> MIME_TO_EXT;

    // common extension -> MIME (not comprehensive)
    private static final Map<String, String> EXT_TO_MIME;

    static {
        EXT_TO_MIME = new HashMap<>();
        
        // Text
        EXT_TO_MIME.put("txt", TEXT_PLAIN);
        EXT_TO_MIME.put("html", TEXT_HTML);
        EXT_TO_MIME.put("htm", TEXT_HTML);
        EXT_TO_MIME.put("css", TEXT_CSS);
        EXT_TO_MIME.put("js", TEXT_JS);
        EXT_TO_MIME.put("json", TEXT_JSON);
        EXT_TO_MIME.put("xml", TEXT_XML);
        EXT_TO_MIME.put("csv", TEXT_CSV);
        EXT_TO_MIME.put("php", TEXT_PHP);
        EXT_TO_MIME.put("xhtml", TEXT_XHTML);
        EXT_TO_MIME.put("md", TEXT_MARKDOWN);

        // Documents
        EXT_TO_MIME.put("pdf", APPLICATION_PDF);
        EXT_TO_MIME.put("pptx", APPLICATION_PPTX);
        EXT_TO_MIME.put("ppt", APPLICATION_PPT);
        EXT_TO_MIME.put("docx", APPLICATION_DOCX);
        EXT_TO_MIME.put("doc", APPLICATION_DOC);
        EXT_TO_MIME.put("xlsx", APPLICATION_XLSX);
        EXT_TO_MIME.put("xls", APPLICATION_XLS);

        // Media
        EXT_TO_MIME.put("mp3", AUDIO_MP3);
        EXT_TO_MIME.put("wav", AUDIO_WAV);
        EXT_TO_MIME.put("ogg", AUDIO_OGG);
        EXT_TO_MIME.put("opus", AUDIO_OPUS);
        EXT_TO_MIME.put("flac", AUDIO_FLAC);
        EXT_TO_MIME.put("mp4", VIDEO_MP4);
        EXT_TO_MIME.put("ogv", VIDEO_OGG);
        EXT_TO_MIME.put("webm", VIDEO_WEBM);
        EXT_TO_MIME.put("avi", VIDEO_AVI);
        EXT_TO_MIME.put("mpeg", VIDEO_MPEG);

        // Images
        EXT_TO_MIME.put("png", IMAGE_PNG);
        EXT_TO_MIME.put("jpg", IMAGE_JPEG);
        EXT_TO_MIME.put("jpeg", IMAGE_JPEG);
        EXT_TO_MIME.put("gif", IMAGE_GIF);
        EXT_TO_MIME.put("svg", IMAGE_SVG);
        EXT_TO_MIME.put("bmp", IMAGE_BMP);
        EXT_TO_MIME.put("ico", IMAGE_ICO);
        EXT_TO_MIME.put("tiff", IMAGE_TIFF);
        EXT_TO_MIME.put("webp", IMAGE_WEBP);
        EXT_TO_MIME.put("apng", IMAGE_APNG);

        // Archives
        EXT_TO_MIME.put("zip", APPLICATION_ZIP);
        EXT_TO_MIME.put("tar", APPLICATION_TAR);
        EXT_TO_MIME.put("gz", APPLICATION_GZ);
        EXT_TO_MIME.put("bz", APPLICATION_BZ);

        // Binary
        EXT_TO_MIME.put("exe", APPLICATION_EXE);
        EXT_TO_MIME.put("bin", APPLICATION_BIN);
        EXT_TO_MIME.put("jar", APPLICATION_JAR);

        // Reverse Map
        MIME_TO_EXT = new HashMap<>();
        for (var entry : EXT_TO_MIME.entrySet()) {
            MIME_TO_EXT.computeIfAbsent(entry.getValue(),
                k -> new HashSet<>()).add(entry.getKey());
        }
    }

    /**
     * Infers the MIME type from the extension.
     * 
     * @param ext The extension, case-insensitive. Should not include
     * the dot.
     * @return The MIME type, or <code>null</code> if the extension is
     * unknown.
     */
    public static String fromExtension(String ext) {
        if (ext == null) return null;
        return EXT_TO_MIME.get(ext.toLowerCase());
    }

    /**
     * Infer the MIME type from an abstract path.
     * 
     * @param path The path. Can be <code>null</code>.
     * @return The MIME type, or <code>null</code> if the extension
     * is unknown.
     */
    public static String fromPath(String path) {
        if (path == null) return null;
        return fromExtension(FileUtils.getExtension(path));
    }

    /**
     * Infer the MIME type from a path.instead
     * 
     * @param path The path. Can be <code>null</code>.
     * @return The MIME type, or <code>null</code> if the extension
     * is unknown.
     */
    public static String fromPath(Path path) {
        if (path == null) return null;
        return fromPath(path.toString());
    }

    /**
     * Infer the MIME type from a file.
     * 
     * @param file The file. Can be <code>null</code>.
     * @return The MIME type, or <code>null</code> if the extension
     * is unknown.
     */
    public static String fromFile(File file) {
        if (file == null) return null;
        return fromPath(file.toString());
    }

    /**
     * Retrieve extensions associated with a MIME type.
     * 
     * @param mime The MIME type.
     * @return The extensions, or <code>null</code> if the MIME type
     * is unknown. The extensions will not include the dot.
     */
    public static Set<String> extensionsFor(String mime) {
        if (mime == null);
        var set = MIME_TO_EXT.get(mime);
        if (set == null) return null;
        return Collections.unmodifiableSet(set);
    }

    /**
     * Infers the extension from the MIME type. If there are multiple
     * extensions, an arbitrary one will be returned.
     * 
     * @param mime The MIME type.
     * @return The extension, or <code>null</code> if the MIME type
     * is unknown. The extension will not include the dot.
     */
    public static String toExtension(String mime) {
        var set = extensionsFor(mime);
        if (set == null) return null;
        return MIME_TO_EXT.get(mime).iterator().next();
    }

    // Don't allow instantiation
    private MIME() {}
}
