package com.smartnote.testing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.smartnote.server.resource.AccessMode;
import com.smartnote.server.resource.FileResourceFactory;
import com.smartnote.server.resource.Resource;

public class VirtualFileSystem  {
    private Map<Path, VirtualFile> files;

    public VirtualFileSystem() {
        this.files = new HashMap<>();
    }

    public FileResourceFactory createResourceFactory() {
        return (path, mode) -> new VirtualFileResource(path, mode);
    }
    
    private class VirtualFileResource implements Resource {
        final Path path;
        final AccessMode mode;

        VirtualFileResource(Path path, AccessMode mode) {
            this.path = path;
            this.mode = mode;
        }           

        @Override
        public InputStream openInputStream() throws SecurityException, IOException {
            if (!mode.hasRead())
                throw new SecurityException("No read permission");
            VirtualFile file = files.get(path);
            if (file == null)
                throw new FileNotFoundException(path.toString());
            return file.openInputStream();
        }

        @Override
        public OutputStream openOutputStream() throws SecurityException, IOException {
            if (!mode.hasWrite())
                throw new SecurityException("No write permission");
            VirtualFile file = files.get(path);
            if (file == null) {
                file = new VirtualFile();
                files.put(path, file);
            }
            return file.openOutputStream();
        }

        @Override
        public void delete() throws SecurityException, IOException {
            if (!mode.hasDelete())
                throw new SecurityException("No delete permission");
            VirtualFile file = files.get(path);
            if (file == null)
                throw new FileNotFoundException(path.toString());
            files.remove(path);
        }
        
    }

    class VirtualFile {
        byte[] data;
        boolean isDirectory;
        
        int readers;
        boolean isOpenedForWriting;

        Object lock;

        VirtualFile() {
            this.data = null;
            this.isDirectory = false;

            this.readers = 0;
            this.isOpenedForWriting = false;
            
            this.lock = new Object();
        }

        OutputStream openOutputStream() throws IOException {
            synchronized (lock) {
                if (isDirectory)
                    throw new IOException("File is a directory");
                return new VirtualFileOutputStream();
            }
        }

        InputStream openInputStream() throws IOException {
            synchronized (lock) {
                if (isDirectory)
                    throw new IOException("File is a directory");
                return new VirtualFileInputStream();
            }
        }

        // light wrapper around ByteArrayOutputStream, so data is
        // not copied until close() is called
        class VirtualFileOutputStream extends OutputStream {
            ByteArrayOutputStream out;

            VirtualFileOutputStream() throws IOException {
                synchronized (lock) {
                    if (VirtualFile.this.readers > 0)
                        throw new IOException("File is already opened for reading");

                    if (VirtualFile.this.isOpenedForWriting)
                        throw new IOException("File is already opened for writing");

                    this.out = new ByteArrayOutputStream();
                    VirtualFile.this.isOpenedForWriting = true;
                }
            }

            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                out.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
            }
            
            @Override
            public void close() throws IOException {
                synchronized (lock) {
                    out.close();
                    data = out.toByteArray();
                    VirtualFile.this.isOpenedForWriting = false;
                }
            }
        }

        // light wrapper around ByteArrayInputStream
        class VirtualFileInputStream extends InputStream {
            ByteArrayInputStream in;

            VirtualFileInputStream() throws IOException {
                synchronized (lock) {
                    if (VirtualFile.this.isOpenedForWriting)
                        throw new IOException("File is already opened for writing");

                    VirtualFile.this.readers++;
                    this.in = new ByteArrayInputStream(data);
                }
            }

            @Override
            public int read() throws IOException {
                return in.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return in.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return in.read(b, off, len);
            }

            @Override
            public void close() throws IOException {
                synchronized (lock) {
                    VirtualFile.this.readers--;
                    in.close();
                }
            }
        }
    }
}
