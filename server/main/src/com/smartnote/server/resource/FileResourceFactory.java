package com.smartnote.server.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@FunctionalInterface
public interface FileResourceFactory {
    Resource openFileResource(Path path, AccessMode mode) throws FileNotFoundException, SecurityException, IOException;
}
