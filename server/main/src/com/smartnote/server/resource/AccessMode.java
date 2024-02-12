package com.smartnote.server.resource;

/**
 * <p>Access mode.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.Resource
 */
public enum AccessMode {
    READ, READ_WRITE, READ_WRITE_DELETE;

    public boolean hasRead() {
        return this == READ || this == READ_WRITE || this == READ_WRITE_DELETE;
    }

    public boolean hasWrite() {
        return this == READ_WRITE || this == READ_WRITE_DELETE;
    }

    public boolean hasDelete() {
        return this == READ_WRITE_DELETE;
    }

    public void checkRead() throws SecurityException {
        if (!hasRead())
            throw new SecurityException("No read permission");
    }

    public void checkWrite() throws SecurityException {
        if (!hasWrite())
            throw new SecurityException("No write permission");
    }

    public void checkDelete() throws SecurityException {
        if (!hasDelete())
            throw new SecurityException("No delete permission");
    }
}
