package com.smartnote.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

/**
 * <p>
 * Represents a file resource.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.Resource
 */
class FileResource implements Resource {

    /**
     * Read permission.
     */
    static final int READ = 1;

    /**
     * Write permission.
     */
    static final int WRITE = 2;

    /**
     * Delete permission.
     */
    static final int DELETE = 4;

    protected final File file;
    protected final int mode;

    /**
     * Creates a new file resource.
     * 
     * @param file The file.
     * @param mode The mode. Bitwise OR of the permissions.
     */
    FileResource(File file, int mode) {
        this.file = Objects.requireNonNull(file, "file must not be null");
        this.mode = mode;
    }

    @Override
    public InputStream openInputStream() throws SecurityException, IOException {
        if ((mode & READ) == 0)
            throw new SecurityException("No read permission");
        return new FileInputStream(file);
    }

    @Override
    public OutputStream openOutputStream() throws SecurityException, IOException {
        if ((mode & WRITE) == 0)
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
        if ((mode & DELETE) == 0)
            throw new SecurityException("No delete permission");
        file.delete();
    }
}
