package com.smartnote.server.resource;

import java.io.File;
import java.io.FileInputStream;
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
        mode.checkRead();
        return new FileInputStream(checkExists());
    }

    @Override
    public OutputStream openOutputStream() throws SecurityException, IOException {
        mode.checkWrite();
        Files.createDirectories(file.getParentFile().toPath());
        return new FileOutputStream(file);
    }

    @Override
    public void delete() throws SecurityException, IOException {
        mode.checkDelete();
        checkExists().delete();
    }

    @Override
    public long size() throws SecurityException, IOException {
        mode.checkRead();
        checkExists();

        if (file.isDirectory())
            return FileUtils.getDirectorySize(file);

        return file.length();
    }

    @Override
    public boolean exists() throws SecurityException {
        mode.checkRead();
        return file.exists();
    }

    @Override
    public String toString() {
        return file.toString();
    }

    private File checkExists() throws NoSuchResourceException {
        if (!file.exists())
            throw new NoSuchResourceException(file.getName());
        return file;
    }
}
