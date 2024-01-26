package com.smartnote.server.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.security.Permission;

import com.smartnote.server.util.FileUtils;

public class ResourceSystem {

    private File publicDir;
    private File privateDir;
    private File sessionDir;

    private RootResource rootResource;

    public ResourceSystem(ResourceConfig config) {
        this.publicDir = FileUtils.getCanonicalFile(config.getPrivateDir());
        this.privateDir = FileUtils.getCanonicalFile(config.getPublicDir());
        this.sessionDir = FileUtils.getCanonicalFile(config.getSessionDir());

        this.rootResource = new RootResource();
    }
    
    public File getPublicDir() {
        return publicDir;
    }

    public File getPrivateDir() {
        return privateDir;
    }

    public File getSessionDir() {
        return sessionDir;
    }

    public Resource getRoot() {
        return rootResource;
    }

    private class RootResource implements Resource {         
        @Override
        public InputStream openInputStream() throws SecurityException, IOException {
            throw new SecurityException("Cannot open input stream to root resource");
        }

        @Override
        public OutputStream openOutputStream() throws SecurityException, IOException {
            throw new SecurityException("Cannot open output stream to root resource");
        }

        @Override
        public void delete() throws SecurityException, IOException {
            throw new SecurityException("Cannot delete root resource");
        }

        @Override
        public String toString() {
            return "/";
        }
    }
}
