package com.smartnote.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

class FileResource implements Resource {
    protected File file;
    
    FileResource(File file) {
        this.file = Objects.requireNonNull(file, "file must not be null");
    }

    @Override
    public InputStream openInputStream() throws SecurityException, IOException {
        return new FileInputStream(file);
    }

    @Override
    public OutputStream openOutputStream() throws SecurityException, IOException {
        return new FileOutputStream(file);
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public void delete() throws SecurityException, IOException {
        file.delete();
    }
}
