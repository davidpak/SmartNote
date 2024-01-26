package com.smartnote.server.security;

import java.security.Permission;

/**
 * <p>Represents the public permission level. Allows read access to
 * public resources.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.ResourceSystem
 * @see com.smartnote.server.security.Identity
 */
public class PublicPermission extends Permission {

    PublicPermission() {
        super("public");
    }

    @Override
    public boolean implies(Permission permission) {
        return permission instanceof PublicPermission;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PublicPermission;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getActions() {
        return "read public";
    }
}
