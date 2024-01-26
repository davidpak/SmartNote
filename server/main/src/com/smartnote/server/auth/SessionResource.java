package com.smartnote.server.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;

import com.smartnote.server.resource.Resource;

public class SessionResource implements Resource {

    public SessionResource(Session session, String path) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public InputStream openInputStream(Permission permission) throws SecurityException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openInputStream'");
    }

    @Override
    public OutputStream openOutputStream(Permission permission) throws SecurityException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openOutputStream'");
    }

    @Override
    public void delete(Permission permission) throws SecurityException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
}
