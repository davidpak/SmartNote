package com.smartnote.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

import com.smartnote.server.util.FileUtils;

/**
 * <p>
 * Represents a file resource.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.Resource
 */
class FileResource implements Resource {
    private final File file;
    private final AccessMode mode;

    /**
     * Creates a new file resource.
     * 
     * @param file The file.
     * @param mode The access mode.
     */
    FileResource(File file, AccessMode mode) {
        this.file = Objects.requireNonNull(file, "file must not be null");
        this.mode = Objects.requireNonNull(mode, "mode must not be null");
    }

    @Override
    public InputStream openInputStream() throws SecurityException, IOException {
        if (!mode.hasRead())
            throw new SecurityException("No read permission");
        if (!file.exists())
            throw new FileNotFoundException("File does not exist");
        return new FileInputStream(file);
    }

    @Override
    public OutputStream openOutputStream() throws SecurityException, IOException {
        if (!mode.hasWrite())
            throw new SecurityException("No write permission");
        Files.createDirectories(file.getParentFile().toPath());
        return new FileOutputStream(file);
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public void delete() throws SecurityException, IOException {
        if (!mode.hasDelete())
            throw new SecurityException("No delete permission");
        if (!file.exists())
            throw new FileNotFoundException("File does not exist");
        file.delete();
    }

    @Override
    public long size() throws SecurityException, IOException {
        if (!mode.hasRead())
            throw new SecurityException("No read permission");

        if (!file.exists())
            throw new FileNotFoundException("File does not exist");

        if (file.isDirectory())
            return FileUtils.getDirectorySize(file);

        return file.length();
    }
}
