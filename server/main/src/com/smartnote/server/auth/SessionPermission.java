package com.smartnote.server.auth;

import java.security.Permission;
import java.util.Objects;

/**
 * <p>
 * Represents a session permission level. Allows read, write, and delete
 * access to resources associated with the session. A session permission
 * implies public permission.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.ResourceSystem
 * @see com.smartnote.server.security.Identity
 */
public class SessionPermission extends Permission {
    private Session session;
    private String actions;

    SessionPermission(Session session) {
        super("session " + Objects.requireNonNull(session, "session cannot be null").getId());
        this.session = session;
        this.actions = "read session " + session.getId() + " write" + session.getId() + " delete" + session.getId();
    }

    @Override
    public boolean implies(Permission permission) {
        return permission.equals(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SessionPermission)
            return ((SessionPermission) obj).session.equals(session);
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String getActions() {
        return actions;
    }

    public Session getSession() {
        return session;
    }
}
